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
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import utils.IdGenerator
import v3.controllers.requestParsers.AmendPeriodSummaryRequestParser
import v3.models.request.amendPeriodSummary.AmendPeriodSummaryRawData
import v3.models.response.amendPeriodSummary.AmendPeriodSummaryHateoasData
import v3.models.response.amendPeriodSummary.AmendPeriodSummaryResponse.LinksFactory
import v3.services.AmendPeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmendPeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              parser: AmendPeriodSummaryRequestParser,
                                              service: AmendPeriodSummaryService,
                                              appConfig: AppConfig,
                                              hateoasFactory: HateoasFactory,
                                              cc: ControllerComponents,
                                              idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendPeriodSummaryController", endpointName = "amendSelfEmploymentPeriodSummary")

  def handleRequest(nino: String, businessId: String, periodId: String, taxYear: Option[String]): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val includeNegatives = FeatureSwitches(appConfig.featureSwitches).isAllowNegativeExpensesEnabled
      val rawData          = AmendPeriodSummaryRawData(nino, businessId, periodId, request.body, taxYear, includeNegatives)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.amendPeriodSummary)
        .withHateoasResult(hateoasFactory)(AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, taxYear.map(TaxYear.fromMtd)))

      requestHandler.handleRequest(rawData)
    }

}