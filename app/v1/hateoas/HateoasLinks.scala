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
import v1.models.hateoas.Link
import v1.models.hateoas.Method._
import v1.models.hateoas.RelType._

trait HateoasLinks {

  //Domain URIs
  private def sampleUri(appConfig: AppConfig, nino: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/sample/$nino/$taxYear"

  private def annualSummaryUri(appConfig: AppConfig, nino: String, businessId: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/${nino}/${businessId}/annual/${taxYear}"

  private def periodicUpdateUri(appConfig: AppConfig, nino: String, businessId: String) =
    s"/${appConfig.apiGatewayContext}/${nino}/${businessId}/period"

  private def periodicUpdateItemUri(appConfig: AppConfig, nino: String, businessId: String, periodId: String) =
    s"/${appConfig.apiGatewayContext}/${nino}/${businessId}/period/${periodId}"

  //Sample links
  def amendSample(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = sampleUri(appConfig, nino, taxYear),
      method = PUT,
      rel = AMEND_SAMPLE_REL
    )

  def retrieveSample(appConfig: AppConfig, nino: String, taxYear: String, isSelf: Boolean): Link =
    if (isSelf) {
      Link(
        href = sampleUri(appConfig, nino, taxYear),
        method = GET,
        rel = SELF
      )
    }
  else {
      Link(
        href = sampleUri(appConfig, nino, taxYear),
        method = GET,
        rel = RETRIEVE_SAMPLE_REL
      )
    }

  def deleteSample(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = sampleUri(appConfig, nino, taxYear),
      method = DELETE,
      rel = DELETE_SAMPLE_REL
    )

  def retrieveAnnualSummary(appConfig: AppConfig, nino: String, businessId: String, taxYear: String): Link =
    Link(href = annualSummaryUri(appConfig, nino, businessId, taxYear), method = GET, rel = SELF)

  def amendAnnualSummary(appConfig: AppConfig, nino: String, businessId: String, taxYear: String): Link =
    Link(href = annualSummaryUri(appConfig, nino, businessId, taxYear), method = PUT, rel = AMEND_ANNUAL_SUMMARY_REL)

  def deleteAnnualSummary(appConfig: AppConfig, nino: String, businessId: String, taxYear: String): Link =
    Link(href = annualSummaryUri(appConfig, nino, businessId, taxYear), method = DELETE, rel = DELETE_ANNUAL_SUMMARY_REL)


  def listPeriodicUpdate(appConfig: AppConfig, nino: String, businessId: String): Link =
    Link(href = periodicUpdateUri(appConfig, nino, businessId), method = GET, rel = SELF)

  def createPeriodicUpdate(appConfig: AppConfig, nino: String, businessId: String): Link =
    Link(href = periodicUpdateUri(appConfig, nino, businessId), method = POST, rel = CREATE_PERIODIC_UPDATE_REL)

  def retrievePeriodicUpdate(appConfig: AppConfig, nino: String, businessId: String, periodId: String): Link =
    Link(href = periodicUpdateItemUri(appConfig, nino, businessId, periodId), method = GET, rel = SELF)

  def amendPeriodicUpdate(appConfig: AppConfig, nino: String, businessId: String, periodId: String): Link =
    Link(href = periodicUpdateItemUri(appConfig, nino, businessId, periodId), method = PUT, rel = AMEND_PERIODIC_UPDATE_REL)

}
