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

import api.controllers.{AuditHandler, AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.hateoas.HateoasFactory
import api.models.domain.{BusinessId, Nino}
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import routing.{Version, Version2}
import utils.IdGenerator
import v2.controllers.validators.CreatePeriodSummaryValidatorFactory
import v2.models.response.createPeriodSummary.CreatePeriodSummaryHateoasData
import v2.services.CreatePeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CreatePeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               validatorFactory: CreatePeriodSummaryValidatorFactory,
                                               service: CreatePeriodSummaryService,
                                               auditService: AuditService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "CreateSelfEmploymentPeriodController", endpointName = "createSelfEmploymentPeriodSummary")

  def handleRequest(nino: String, businessId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val includeNegatives = FeatureSwitches(appConfig).isAllowNegativeExpensesEnabled
      val validator        = validatorFactory.validator(nino, businessId, request.body, includeNegatives)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.createPeriodSummary)
        .withAuditing(AuditHandler(
          auditService,
          auditType = "CreatePeriodicEmployment",
          transactionName = "self-employment-periodic-create",
          apiVersion = Version.from(request, orElse = Version2),
          params = Map("nino" -> nino, "businessId" -> businessId),
          Some(request.body)
        ))
        .withHateoasResultFrom(hateoasFactory)((parsedRequest, response) =>
          CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), response.periodId, Some(parsedRequest.taxYear)))

      requestHandler.handleRequest()
    }

}
