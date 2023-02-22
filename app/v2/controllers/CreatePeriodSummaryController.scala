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

package v2.controllers

import api.controllers.{AuthorisedController, BaseController, EndpointLogContext}
import api.hateoas.HateoasFactory
import api.models.errors._
import api.models.request.createPeriodSummary._
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import cats.data.EitherT
import cats.implicits._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents, Result}
import utils.{IdGenerator, Logging}
import v2.controllers.requestParsers.CreatePeriodSummaryRequestParser
import v2.models.response.createPeriodSummary.CreatePeriodSummaryHateoasData
import v2.models.response.createPeriodSummary.CreatePeriodSummaryResponse.LinksFactory
import v2.services.CreatePeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreatePeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               parser: CreatePeriodSummaryRequestParser,
                                               service: CreatePeriodSummaryService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "CreateSelfEmploymentPeriodController", endpointName = "createSelfEmploymentPeriodSummary")

  def handleRequest(nino: String, businessId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(
        message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with correlationId : $correlationId")
      val rawData = CreatePeriodSummaryRawData(nino, businessId, request.body)
      val result =
        for {
          parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
          serviceResponse <- EitherT(service.createPeriodicSummary(parsedRequest))
        } yield {
          val hateoasData = CreatePeriodSummaryHateoasData(
            parsedRequest.nino,
            parsedRequest.businessId,
            serviceResponse.responseData.periodId,
            Some(parsedRequest.taxYear)
          )

          val vendorResponse = hateoasFactory.wrap(serviceResponse.responseData, hateoasData)

          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          Ok(Json.toJson(vendorResponse))
            .withApiHeaders(serviceResponse.correlationId)
        }

      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result: Result   = errorResult(errorWrapper).withApiHeaders(resCorrelationId)

        logger.warn(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")
        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) =
    errorWrapper.error match {
      case MtdErrorWithCode(ValueFormatError.code) | MtdErrorWithCode(RuleIncorrectOrEmptyBodyError.code) =>
        BadRequest(Json.toJson(errorWrapper))

      case _
          if errorWrapper.containsAnyOf(
            BadRequestError,
            NinoFormatError,
            ValueFormatError,
            BusinessIdFormatError,
            StartDateFormatError,
            EndDateFormatError,
            RuleBothExpensesSuppliedError,
            RuleEndDateBeforeStartDateError,
            RuleOverlappingPeriod,
            RuleMisalignedPeriod,
            RuleNotContiguousPeriod,
            RuleNotAllowedConsolidatedExpenses,
            RuleIncorrectOrEmptyBodyError,
            RuleDuplicateSubmissionError,
            RuleTaxYearNotSupportedError,
            RuleInvalidSubmissionPeriodError,
            RuleInvalidSubmissionEndDateError
          ) =>
        BadRequest(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case InternalError => InternalServerError(Json.toJson(errorWrapper))
      case _             => unhandledError(errorWrapper)
    }

}
