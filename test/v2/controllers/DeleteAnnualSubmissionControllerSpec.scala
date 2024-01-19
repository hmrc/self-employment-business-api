/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v2.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import play.api.mvc.Result
import v2.controllers.validators.MockDeleteAnnualSubmissionValidatorFactory
import v2.models.request.deleteAnnual.DeleteAnnualSubmissionRequestData
import v2.services.MockDeleteAnnualSubmissionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteAnnualSubmissionControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockDeleteAnnualSubmissionService
    with MockDeleteAnnualSubmissionValidatorFactory {

  private val taxYear: String    = "2019-20"
  private val businessId: String = "XAIS12345678910"
  private val requestData        = DeleteAnnualSubmissionRequestData(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear))

  "handleRequest" should {
    "return NoContent" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockDeleteAnnualSubmissionService
          .deleteAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTest(
          expectedStatus = NO_CONTENT
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockDeleteAnnualSubmissionService
          .deleteAnnualSubmission(requestData)
          .returns(Future.successful(Left(errors.ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new DeleteAnnualSubmissionController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockDeleteAnnualSubmissionValidatorFactory,
      service = mockDeleteAnnualSubmissionService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakeRequest)
  }

}
