/*
 * Copyright 2026 HM Revenue & Customs
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

package v5.createAmendAnnualSubmission

import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v5.createAmendAnnualSubmission.def1.model.request.Def1_CreateAmendAnnualSubmissionRequestBody
import v5.createAmendAnnualSubmission.def3.request.Def3_CreateAmendAnnualSubmissionRequestBody
import v5.createAmendAnnualSubmission.model.request.*

import scala.concurrent.Future

class CreateAmendAnnualSubmissionConnectorSpec extends ConnectorSpec {

  private val nino: String       = "AA123456A"
  private val businessId: String = "XAIS12345678910"

  private val def1Body: Def1_CreateAmendAnnualSubmissionRequestBody = Def1_CreateAmendAnnualSubmissionRequestBody(None, None, None)
  private val def3Body: Def3_CreateAmendAnnualSubmissionRequestBody = Def3_CreateAmendAnnualSubmissionRequestBody(None, None, None)

  private trait Test {
    self: ConnectorTest =>

    val def1Request: CreateAmendAnnualSubmissionRequestData = Def1_CreateAmendAnnualSubmissionRequestData(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      taxYear = taxYear,
      body = def1Body
    )

    val def3Request: CreateAmendAnnualSubmissionRequestData = Def3_CreateAmendAnnualSubmissionRequestData(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      taxYear = taxYear,
      body = def3Body
    )

    protected val connector: CreateAmendAnnualSubmissionConnector = new CreateAmendAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

    def taxYear: TaxYear
  }

  "CreateAmendAnnualSubmissionConnector" when {
    ".amendAnnualSubmission" should {
      "return a 200 status for a success scenario" when {
        "a valid request with a non-TYS tax year is supplied" in new IfsTest with Test {
          def taxYear: TaxYear = TaxYear.fromMtd("2019-20")

          val outcome: DownstreamOutcome[Unit] = Right(ResponseWrapper(correlationId, ()))

          willPut(
            url = url"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}",
            body = def1Body
          ).returns(Future.successful(outcome))

          val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(def1Request))

          result shouldBe outcome
        }

        "a valid request with a TYS tax year before 2025-26 is supplied" in new IfsTest with Test {
          def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

          val outcome: DownstreamOutcome[Unit] = Right(ResponseWrapper(correlationId, ()))

          willPut(
            url = url"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries",
            body = def1Body
          ).returns(Future.successful(outcome))

          val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(def1Request))

          result shouldBe outcome
        }

        "a valid request with a TYS tax year 2025-26 is supplied and feature switch is disabled (IFS enabled)" in new IfsTest with Test {
          def taxYear: TaxYear = TaxYear.fromMtd("2025-26")

          val outcome: DownstreamOutcome[Unit] = Right(ResponseWrapper(correlationId, ()))

          MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1802.enabled" -> false))

          willPut(
            url = url"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries",
            body = def3Body
          ).returns(Future.successful(outcome))

          val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(def3Request))

          result shouldBe outcome
        }

        "a valid request with a TYS tax year 2025-26 is supplied and feature switch is enabled (HIP enabled)" in new HipTest with Test {
          def taxYear: TaxYear = TaxYear.fromMtd("2025-26")

          val outcome: DownstreamOutcome[Unit] = Right(ResponseWrapper(correlationId, ()))

          MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1802.enabled" -> true))

          willPut(
            url = url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries",
            body = def3Body
          ).returns(Future.successful(outcome))

          val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(def3Request))

          result shouldBe outcome
        }
      }
    }
  }
  
}
