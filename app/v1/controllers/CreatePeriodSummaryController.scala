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

package v1.controllers

import api.controllers.RequestContextImplicits.toCorrelationId
import api.controllers.{AuditHandler, AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.hateoas.HateoasFactory
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.CreatePeriodSummaryRequestParser
import v1.models.request.createPeriodSummary.CreatePeriodSummaryRawData
import v1.models.response.createPeriodSummary.CreatePeriodSummaryHateoasData
import v1.models.response.createPeriodSummary.CreatePeriodSummaryResponse.LinksFactory
import v1.services.CreatePeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CreatePeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               parser: CreatePeriodSummaryRequestParser,
                                               service: CreatePeriodSummaryService,
                                               hateoasFactory: HateoasFactory,
                                               auditService: AuditService,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "CreateSelfEmploymentPeriodController", endpointName = "createSelfEmploymentPeriodSummary")

  def handleRequest(nino: String, businessId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = CreatePeriodSummaryRawData(nino, businessId, request.body)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.createPeriodicSummary)
        .withAuditing(AuditHandler(
          auditService,
          auditType = "CreatePeriodSummary",
          transactionName = "create-period-summary",
          pathParams = Map("nino" -> nino, "businessId" -> businessId),
          includeResponse = true
        ))
        .withHateoasResult(hateoasFactory)(CreatePeriodSummaryHateoasData(nino, businessId, periodId, Some(taxYear)))

      requestHandler.handleRequest(rawData)
    }

}
