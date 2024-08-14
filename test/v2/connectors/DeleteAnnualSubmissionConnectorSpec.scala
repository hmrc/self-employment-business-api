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

package v2.connectors

import config.MockSeBusinessFeatureSwitches
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import org.scalamock.handlers.CallHandler
import play.api.libs.json.JsObject
import v2.models.request.deleteAnnual.DeleteAnnualSubmissionRequestData

import scala.concurrent.Future

class DeleteAnnualSubmissionConnectorSpec extends ConnectorSpec with MockSeBusinessFeatureSwitches {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"

  private val preTysTaxYear = TaxYear.fromMtd("2017-18")
  private val tysTaxYear    = TaxYear.fromMtd("2023-24")

  "deleteAnnualSubmissionConnector" when {
    val outcome = Right(ResponseWrapper(correlationId, ()))

    "deleteAnnualSubmission is called with a valid request and a non-TYS tax year" when {
      "`isPassDeleteIntentEnabled` feature switch is on" must {
        "send a request and return 204 no content" in new DesTest with Test {
          override lazy val intent: Option[String] = Some("DELETE")

          def taxYear: TaxYear = preTysTaxYear

          stubHttpResponse(outcome)
          MockedSeBusinessFeatureSwitches.isPassDeleteIntentEnabled.returns(true)

          await(connector.deleteAnnualSubmission(request)) shouldBe outcome
        }
      }
      "`isPassDeleteIntentEnabled` feature switch is off" must {
        "send a request and return 204 no content" in new DesTest with Test {
          def taxYear: TaxYear = preTysTaxYear

          stubHttpResponse(outcome)
          MockedSeBusinessFeatureSwitches.isPassDeleteIntentEnabled.returns(false)

          await(connector.deleteAnnualSubmission(request)) shouldBe outcome
        }
      }
    }

    "deleteAnnualSubmission is called with a TYS tax year" must {
      "send a request and return 204 no content" in new TysIfsTest with Test {
        def taxYear: TaxYear = tysTaxYear

        stubTysHttpResponse(outcome)
        MockedSeBusinessFeatureSwitches.isPassDeleteIntentEnabled.returns(false)

        await(connector.deleteAnnualSubmission(request)) shouldBe outcome
      }
    }

    "response is an error" must {
      val downstreamErrorResponse: DownstreamErrors = DownstreamErrors.single(DownstreamErrorCode("SOME_ERROR"))

      val outcome = Left(ResponseWrapper(correlationId, downstreamErrorResponse))

      "return the error" in new DesTest with Test {
        def taxYear: TaxYear = preTysTaxYear
        stubHttpResponse(outcome)
        MockedSeBusinessFeatureSwitches.isPassDeleteIntentEnabled.returns(false)

        val result: DownstreamOutcome[Unit] =
          await(connector.deleteAnnualSubmission(request))
        result shouldBe outcome
      }

      "return the error given a TYS tax year request" in new TysIfsTest with Test {
        def taxYear: TaxYear = tysTaxYear
        stubTysHttpResponse(outcome)
        MockedSeBusinessFeatureSwitches.isPassDeleteIntentEnabled.returns(false)

        val result: DownstreamOutcome[Unit] =
          await(connector.deleteAnnualSubmission(request))
        result shouldBe outcome
      }
    }
  }

  trait Test { _: ConnectorTest =>

    def taxYear: TaxYear

    val connector: DeleteAnnualSubmissionConnector = new DeleteAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    val request: DeleteAnnualSubmissionRequestData = DeleteAnnualSubmissionRequestData(
      nino = Nino(nino),
      taxYear = taxYear,
      businessId = BusinessId(businessId)
    )

    protected def stubHttpResponse(outcome: DownstreamOutcome[Unit]): CallHandler[Future[DownstreamOutcome[Unit]]]#Derived = {
      willPut(
        url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}",
        body = JsObject.empty
      ).returns(Future.successful(outcome))
    }

    protected def stubTysHttpResponse(outcome: DownstreamOutcome[Unit]): CallHandler[Future[DownstreamOutcome[Unit]]]#Derived = {
      willDelete(
        url = s"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"
      ).returns(Future.successful(outcome))
    }

  }

}
