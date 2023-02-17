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
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.DeleteAnnualSubmissionRequestParser
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRawData
import v1.services.DeleteAnnualSubmissionService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DeleteAnnualSubmissionController @Inject() (val authService: EnrolmentsAuthService,
                                                  val lookupService: MtdIdLookupService,
                                                  parser: DeleteAnnualSubmissionRequestParser,
                                                  service: DeleteAnnualSubmissionService,
                                                  auditService: AuditService,
                                                  cc: ControllerComponents,
                                                  idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "DeleteAnnualSubmissionController", endpointName = "deleteSelfEmploymentAnnualSubmission")

  def handleRequest(nino: String, businessId: String, taxYear: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = DeleteAnnualSubmissionRawData(nino, businessId, taxYear)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.deleteAnnualSubmission)
        .withNoContentResult()
        .withAuditing(AuditHandler(
          auditService,
          auditType = "DeleteAnnualSubmission",
          transactionName = "delete-annual-submission",
          pathParams = Map("nino" -> nino, "businessId" -> businessId, "taxYear" -> taxYear)
        ))

      requestHandler.handleRequest(rawData)

    }

}
