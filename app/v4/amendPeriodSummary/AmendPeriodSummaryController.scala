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

package v4.amendPeriodSummary

import config.SeBusinessFeatureSwitches
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import shared.config.SharedAppConfig
import shared.controllers._
import shared.routing.Version
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.IdGenerator

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmendPeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              validatorFactory: AmendPeriodSummaryValidatorFactory,
                                              service: AmendPeriodSummaryService,
                                              auditService: AuditService,
                                              cc: ControllerComponents,
                                              idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: SharedAppConfig)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendPeriodSummaryController", endpointName = "amendSelfEmploymentPeriodSummary")

  val endpointName = "amend-period-summary"

  def handleRequest(nino: String, businessId: String, periodId: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val includeNegatives = SeBusinessFeatureSwitches().isAllowNegativeExpensesEnabled
      val validator        = validatorFactory.validator(nino, businessId, periodId, taxYear, request.body, includeNegatives)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.amendPeriodSummary)
        .withAuditing(AuditHandler(
          auditService,
          auditType = "AmendPeriodicEmployment",
          transactionName = "self-employment-periodic-amend",
          apiVersion = Version(request),
          params = Map("nino" -> nino, "businessId" -> businessId, "periodId" -> periodId, "taxYear" -> taxYear),
          Some(request.body)
        ))
        .withNoContentResult(OK)

      requestHandler.handleRequest()
    }

}