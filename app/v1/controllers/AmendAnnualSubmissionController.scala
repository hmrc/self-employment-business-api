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
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.AmendAnnualSubmissionRequestParser
import v1.models.request.amendSEAnnual.AmendAnnualSubmissionRawData
import v1.models.response.amendSEAnnual.AmendAnnualSubmissionHateoasData
import v1.models.response.amendSEAnnual.AmendAnnualSubmissionResponse._
import v1.services.AmendAnnualSubmissionService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendAnnualSubmissionController @Inject() (val authService: EnrolmentsAuthService,
                                                 val lookupService: MtdIdLookupService,
                                                 parser: AmendAnnualSubmissionRequestParser,
                                                 service: AmendAnnualSubmissionService,
                                                 hateoasFactory: HateoasFactory,
                                                 cc: ControllerComponents,
                                                 idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendAnnualSubmissionController", endpointName = "amendSelfEmploymentAnnualSubmission")

  def handleRequest(nino: String, businessId: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = AmendAnnualSubmissionRawData(nino, businessId, taxYear, request.body)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.amendAnnualSubmission)
        .withPlainJsonResult()
        .withAuditing(AuditHandler(
          auditService,
          auditType = "AmendAnnualSubmission",
          transactionName = "amend-annual-submission",
          pathParams = Map("nino" -> nino, "businessId" -> businessId, "taxYear" -> taxYear),
          includeResponse = true
        ))

      requestHandler.handleRequest(rawData)
    }

}
