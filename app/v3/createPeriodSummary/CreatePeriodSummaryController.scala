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

package v3.createPeriodSummary

import config.SeBusinessFeatureSwitches
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import shared.config.SharedAppConfig
import shared.controllers.*
import shared.hateoas.HateoasFactory
import shared.models.domain.{BusinessId, Nino}
import shared.routing.Version
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.IdGenerator
import v3.createPeriodSummary.model.response.CreatePeriodSummaryHateoasData

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
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: SharedAppConfig)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "CreatePeriodSummaryController", endpointName = "createSelfEmploymentPeriodSummary")

  val endpointName = "create-period-summary"

  private val featureSwitches = SeBusinessFeatureSwitches()

  def handleRequest(nino: String, businessId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val includeNegatives = featureSwitches.isAllowNegativeExpensesEnabled
      val validator        = validatorFactory.validator(nino, businessId, request.body, includeNegatives)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.createPeriodSummary)
        .withAuditing(AuditHandler(
          auditService,
          auditType = "CreatePeriodicEmployment",
          transactionName = "self-employment-periodic-create",
          apiVersion = Version(request),
          params = Map("nino" -> nino, "businessId" -> businessId),
          Some(request.body)
        ))
        .withHateoasResultFrom(hateoasFactory)((parsedRequest, response) =>
          CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), response.periodId, Some(parsedRequest.taxYear)))

      requestHandler.handleRequest()
    }

}
