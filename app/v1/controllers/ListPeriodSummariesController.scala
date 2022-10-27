/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.ListPeriodSummariesRequestParser
import v1.hateoas.HateoasFactory
import v1.models.errors._
import v1.models.request.listPeriodSummaries.ListPeriodSummariesRawData
import v1.models.response.listPeriodSummaries.ListPeriodSummariesHateoasData
import v1.services.{EnrolmentsAuthService, ListPeriodSummariesService, MtdIdLookupService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListPeriodSummariesController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               parser: ListPeriodSummariesRequestParser,
                                               service: ListPeriodSummariesService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "ListPeriodSummariesController", endpointName = "listSelfEmploymentPeriodSummaries")

  def handleRequest(nino: String, businessId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId

      logger.info(
        message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with correlationId : $correlationId")
      val rawData = ListPeriodSummariesRawData(nino, businessId, taxYear)
      val result =
        for {
          parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
          serviceResponse <- EitherT(service.listPeriodSummaries(parsedRequest))
        } yield {
          val hateoasData = ListPeriodSummariesHateoasData(parsedRequest.nino, parsedRequest.businessId, parsedRequest.taxYear)
          val vendorResponse = hateoasFactory.wrapList(serviceResponse.responseData, hateoasData)

          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          Ok(Json.toJson(vendorResponse))
            .withApiHeaders(serviceResponse.correlationId)
        }
      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result           = errorResult(errorWrapper).withApiHeaders(resCorrelationId)

        logger.warn(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")
        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) =
    errorWrapper.error match {
      case TaxYearFormatError | RuleTaxYearRangeInvalidError | RuleTaxYearNotSupportedError | NinoFormatError | BusinessIdFormatError |
          BadRequestError | InvalidTaxYearParameterError =>
        BadRequest(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case InternalError => InternalServerError(Json.toJson(errorWrapper))
      case _             => unhandledError(errorWrapper)
    }

}
