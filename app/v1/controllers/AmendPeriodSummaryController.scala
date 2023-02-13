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

import api.controllers.{AuthorisedController, BaseController, EndpointLogContext}
import api.hateoas.HateoasFactory
import api.models.errors._
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import cats.data.EitherT
import cats.implicits._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.AmendPeriodSummaryRequestParser
import v1.models.request.amendPeriodSummary.AmendPeriodSummaryRawData
import v1.models.response.amendPeriodSummary.AmendPeriodSummaryHateoasData
import v1.models.response.amendPeriodSummary.AmendPeriodSummaryResponse.LinksFactory
import v1.services.AmendPeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendPeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              parser: AmendPeriodSummaryRequestParser,
                                              service: AmendPeriodSummaryService,
                                              hateoasFactory: HateoasFactory,
                                              cc: ControllerComponents,
                                              idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendPeriodSummaryController", endpointName = "amendSelfEmploymentPeriodSummary")

  def handleRequest(nino: String, businessId: String, periodId: String, taxYear: Option[String]): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(
        message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with correlationId : $correlationId")
      val rawData = AmendPeriodSummaryRawData(nino, businessId, periodId, request.body, taxYear)
      val result =
        for {
          parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
          serviceResponse <- EitherT(service.amendPeriodSummary(parsedRequest))
        } yield {
          val hateoasData    = AmendPeriodSummaryHateoasData(parsedRequest.nino, parsedRequest.businessId, periodId, parsedRequest.taxYear)
          val vendorResponse = hateoasFactory.wrap(serviceResponse.responseData, hateoasData)

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
      case _
          if errorWrapper.containsAnyOf(
            BadRequestError,
            NinoFormatError,
            BusinessIdFormatError,
            PeriodIdFormatError,
            RuleBothExpensesSuppliedError,
            RuleNotAllowedConsolidatedExpenses,
            ValueFormatError,
            RuleIncorrectOrEmptyBodyError,
            RuleTaxYearNotSupportedError,
            InvalidTaxYearParameterError,
            TaxYearFormatError,
            RuleTaxYearRangeInvalidError
          ) =>
        BadRequest(Json.toJson(errorWrapper))

      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case InternalError => InternalServerError(Json.toJson(errorWrapper))
      case _             => unhandledError(errorWrapper)
    }

}
