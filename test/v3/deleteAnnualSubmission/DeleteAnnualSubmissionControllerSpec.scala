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

package v3.deleteAnnualSubmission

import config.MockSeBusinessConfig
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import v3.deleteAnnualSubmission
import v3.deleteAnnualSubmission.model.Def1_DeleteAnnualSubmissionRequestData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteAnnualSubmissionControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockDeleteAnnualSubmissionService
    with MockSeBusinessConfig
    with deleteAnnualSubmission.MockDeleteAnnualSubmissionValidatorFactory {

  private val taxYear: String    = "2019-20"
  private val businessId: String = "XAIS12345678910"
  private val requestData        = Def1_DeleteAnnualSubmissionRequestData(Nino(validNino), BusinessId(businessId), TaxYear.fromMtd(taxYear))

  "handleRequest" should {
    "return NoContent" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockedDeleteAnnualSubmissionService
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

        MockedDeleteAnnualSubmissionService
          .deleteAnnualSubmission(requestData)
          .returns(Future.successful(Left(errors.ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    private val controller = new DeleteAnnualSubmissionController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockDeleteAnnualSubmissionValidatorFactory,
      service = mockDeleteAnnualSubmissionService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, taxYear)(fakeRequest)
  }

}
