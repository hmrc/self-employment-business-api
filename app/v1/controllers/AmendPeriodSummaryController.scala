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
import api.controllers._
import api.hateoas.HateoasFactory
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.AmendPeriodSummaryRequestParser
import v1.models.request.amendPeriodSummary.AmendPeriodSummaryRawData
import v1.models.response.amendPeriodSummary.AmendPeriodSummaryHateoasData
import v1.services._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class AmendPeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              parser: AmendPeriodSummaryRequestParser,
                                              service: AmendPeriodSummaryService,
                                              hateoasFactory: HateoasFactory,
                                              auditService: AuditService,
                                              cc: ControllerComponents,
                                              idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendPeriodSummaryController", endpointName = "amendSelfEmploymentPeriodSummary")

  def handleRequest(nino: String, businessId: String, periodId: String, taxYear: Option[String]): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = AmendPeriodSummaryRawData(nino, businessId, periodId, request.body, taxYear)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.amendPeriodSummary)
        .withAuditing(AuditHandler(
          auditService,
          auditType = "AmendPeriodSummary",
          transactionName = "amend-period-summary",
          pathParams = Map("nino" -> nino, "businessId" -> businessId, "periodId" -> periodId, "taxYear" -> taxYear),
          requestBody = Some(request.body),
          includeResponse = true
        ))
        .withHateoasResult(hateoasFactory)(AmendPeriodSummaryHateoasData(nino, businessId, periodId, taxYear))

      requestHandler.handleRequest(rawData)
    }

}
