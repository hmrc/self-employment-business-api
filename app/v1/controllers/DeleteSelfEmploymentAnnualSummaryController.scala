/*
 * Copyright 2020 HM Revenue & Customs
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

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.Logging
import v1.controllers.requestParsers.DeleteSelfEmploymentAnnualSummaryRequestParser
import v1.models.errors._
import v1.models.request.deleteSEAnnual.DeleteSelfEmploymentAnnualSummaryRawData
import v1.services.{DeleteSelfEmploymentAnnualSummaryService, EnrolmentsAuthService, MtdIdLookupService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteSelfEmploymentAnnualSummaryController @Inject()(val authService: EnrolmentsAuthService,
                                                            val lookupService: MtdIdLookupService,
                                                            parser: DeleteSelfEmploymentAnnualSummaryRequestParser,
                                                            service: DeleteSelfEmploymentAnnualSummaryService,
                                                            cc: ControllerComponents)(implicit ec: ExecutionContext)
extends AuthorisedController(cc) with BaseController with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "DeleteSelfEmploymentAnnualSummaryController", endpointName = "deleteSelfEmploymentAnnualSummary")

  def handleRequest(nino: String, businessId: String, taxYear: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = DeleteSelfEmploymentAnnualSummaryRawData(nino, businessId, taxYear)
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](parser.parseRequest(rawData))
          serviceResponse <- EitherT(service.deleteSelfEmploymentAnnualSummary(parsedRequest))
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          NoContent.withApiHeaders(serviceResponse.correlationId)

        }
      result.leftMap { errorWrapper =>
        val correlationId = getCorrelationId(errorWrapper)
        errorResult(errorWrapper).withApiHeaders(correlationId)
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case NinoFormatError |
           BadRequestError |
           TaxYearFormatError |
           RuleTaxYearNotSupportedError |
           RuleTaxYearRangeInvalidError => BadRequest(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
    }
  }
}