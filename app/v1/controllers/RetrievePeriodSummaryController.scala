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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.RetrievePeriodSummaryRequestParser
import v1.models.request.retrievePeriodSummary.RetrievePeriodSummaryRawData
import v1.models.response.retrievePeriodSummary.RetrievePeriodSummaryHateoasData
import v1.services.RetrievePeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RetrievePeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                                 val lookupService: MtdIdLookupService,
                                                 parser: RetrievePeriodSummaryRequestParser,
                                                 service: RetrievePeriodSummaryService,
                                                 hateoasFactory: HateoasFactory,
                                                 auditService: AuditService,
                                                 cc: ControllerComponents,
                                                 idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "RetrievePeriodSummaryController", endpointName = "retrieveSelfEmploymentPeriodicSummary")

  def handleRequest(nino: String, businessId: String, periodId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = RetrievePeriodSummaryRawData(nino, businessId, periodId, taxYear)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.retrievePeriodSummary)
        .withPlainJsonResult()
        .withAuditing(AuditHandler(
          auditService = auditService,
          auditType = "RetrievePeriodSummary",
          transactionName = "retrieve-period-summary",
          pathParams = Map("nino" -> nino, "businessId" -> businessId, "periodId" -> periodId, "taxYear" -> taxYear),
          includeResponse = true
        ))

      requestHandler.handleRequest(rawData)
    }

}
