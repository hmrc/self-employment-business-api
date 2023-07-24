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

package v3.controllers

import api.controllers.{AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.hateoas.HateoasFactory
import api.models.domain.{BusinessId, Nino}
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import utils.IdGenerator
import v3.controllers.requestParsers.CreatePeriodSummaryRequestParser
import v3.models.request.createPeriodSummary.CreatePeriodSummaryRawData
import v3.models.response.createPeriodSummary.CreatePeriodSummaryHateoasData
import v3.services.CreatePeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CreatePeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               parser: CreatePeriodSummaryRequestParser,
                                               service: CreatePeriodSummaryService,
                                               appConfig: AppConfig,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "CreatePeriodSummaryController", endpointName = "createSelfEmploymentPeriodSummary")

  def handleRequest(nino: String, businessId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val includeNegatives = FeatureSwitches(appConfig.featureSwitches).isAllowNegativeExpensesEnabled
      val rawData          = CreatePeriodSummaryRawData(nino, businessId, request.body, includeNegatives)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.createPeriodSummary)
        .withHateoasResultFrom(hateoasFactory)((parsedRequest, response) =>
          CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), response.periodId, Some(parsedRequest.taxYear)))

      requestHandler.handleRequest(rawData)
    }

}
