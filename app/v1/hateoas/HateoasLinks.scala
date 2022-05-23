/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.hateoas

import config.AppConfig
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.hateoas.Link
import v1.models.hateoas.Method._
import v1.models.hateoas.RelType._

trait HateoasLinks {

  // Domain URIs
  private def annualSubmissionUri(appConfig: AppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear) =
    s"/${appConfig.apiGatewayContext}/${nino.nino}/${businessId.value}/annual/${taxYear.toMtd}"

  private def periodicSummaryUri(appConfig: AppConfig, nino: Nino, businessId: BusinessId) =
    s"/${appConfig.apiGatewayContext}/${nino.nino}/${businessId.value}/period"

  private def periodicSummaryItemUri(appConfig: AppConfig, nino: Nino, businessId: BusinessId, periodId: String) =
    s"/${appConfig.apiGatewayContext}/${nino.nino}/${businessId.value}/period/$periodId"

  def retrieveAnnualSubmission(appConfig: AppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear): Link =
    Link(href = annualSubmissionUri(appConfig, nino, businessId, taxYear), method = GET, rel = SELF)

  def amendAnnualSubmission(appConfig: AppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear): Link =
    Link(href = annualSubmissionUri(appConfig, nino, businessId, taxYear), method = PUT, rel = AMEND_ANNUAL_SUBMISSION_REL)

  def deleteAnnualSubmission(appConfig: AppConfig, nino: Nino, businessId: BusinessId, taxYear: TaxYear): Link =
    Link(href = annualSubmissionUri(appConfig, nino, businessId, taxYear), method = DELETE, rel = DELETE_ANNUAL_SUBMISSION_REL)

  def listPeriodicSummary(appConfig: AppConfig, nino: Nino, businessId: BusinessId, isSelf: Boolean): Link =
    Link(href = periodicSummaryUri(appConfig, nino, businessId), method = GET, rel = if (isSelf) SELF else LIST_PERIOD_SUMMARIES_REL)

  def createPeriodicSummary(appConfig: AppConfig, nino: Nino, businessId: BusinessId): Link =
    Link(href = periodicSummaryUri(appConfig, nino, businessId), method = POST, rel = CREATE_PERIOD_SUMMARY_REL)

  def retrievePeriodicSummary(appConfig: AppConfig, nino: Nino, businessId: BusinessId, periodId: String): Link =
    Link(href = periodicSummaryItemUri(appConfig, nino, businessId, periodId), method = GET, rel = SELF)

  def amendPeriodicSummary(appConfig: AppConfig, nino: Nino, businessId: BusinessId, periodId: String): Link =
    Link(href = periodicSummaryItemUri(appConfig, nino, businessId, periodId), method = PUT, rel = AMEND_PERIOD_SUMMARY_REL)

}
