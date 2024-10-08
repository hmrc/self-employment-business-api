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

package api.hateoas

import api.hateoas.RelType._
import shared.config.SharedAppConfig
import shared.hateoas.Link
import shared.hateoas.Method.{DELETE, GET, POST, PUT}
import shared.models.domain.{BusinessId, Nino, TaxYear}

trait HateoasLinks {

  def retrieveAnnualSubmission(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear): Link =
    Link(href = annualSubmissionUri(appConfig, nino, businessId, taxYear), method = GET, rel = SELF)

  def amendAnnualSubmission(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear): Link =
    Link(href = annualSubmissionUri(appConfig, nino, businessId, taxYear), method = PUT, rel = AMEND_ANNUAL_SUBMISSION_REL)

  def deleteAnnualSubmission(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear): Link =
    Link(href = annualSubmissionUri(appConfig, nino, businessId, taxYear), method = DELETE, rel = DELETE_ANNUAL_SUBMISSION_REL)

  // Domain URIs
  private def annualSubmissionUri(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear) =
    s"/${appConfig.apiGatewayContext}/$nino/$businessId/annual/${taxYear.asMtd}"

  def listPeriodSummaries(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, taxYear: Option[TaxYear], isSelf: Boolean): Link = {
    Link(href = periodSummaryUri(appConfig, nino, businessId, taxYear), method = GET, rel = if (isSelf) SELF else LIST_PERIOD_SUMMARIES_REL)
  }

  private def periodSummaryUri(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, taxYear: Option[TaxYear]): String =
    withTaxYearParameter(
      uri = s"/${appConfig.apiGatewayContext}/$nino/$businessId/period",
      taxYear
    )

  private def withTaxYearParameter(uri: String, maybeTaxYear: Option[TaxYear]): String = {
    maybeTaxYear match {
      case Some(taxYear) if taxYear.useTaxYearSpecificApi => s"$uri?taxYear=${taxYear.asMtd}"
      case _                                              => uri
    }
  }

  def createPeriodSummary(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId): Link =
    Link(href = periodSummaryUri(appConfig, nino, businessId, None), method = POST, rel = CREATE_PERIOD_SUMMARY_REL)

  def retrievePeriodSummary(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, periodId: String, taxYear: Option[TaxYear]): Link =
    Link(href = periodSummaryItemUri(appConfig, nino, businessId, periodId, taxYear), method = GET, rel = SELF)

  def amendPeriodSummary(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, periodId: String, taxYear: Option[TaxYear]): Link =
    Link(href = periodSummaryItemUri(appConfig, nino, businessId, periodId, taxYear), method = PUT, rel = AMEND_PERIOD_SUMMARY_REL)

  private def periodSummaryItemUri(appConfig: SharedAppConfig, nino: Nino, businessId: BusinessId, periodId: String, taxYear: Option[TaxYear]) =
    withTaxYearParameter(
      uri = s"/${appConfig.apiGatewayContext}/$nino/$businessId/period/$periodId",
      taxYear
    )

}
