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
import config.AppConfig
import javax.inject.{Inject, Singleton}
import play.api.http.MimeTypes
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.Logging
import v1.controllers.requestParsers.AmendSampleRequestParser
import v1.hateoas.AmendHateoasBodies
import v1.models.audit.{AuditEvent, SampleAuditDetail, SampleAuditResponse}
import v1.models.auth.UserDetails
import v1.models.errors._
import v1.models.request.amendSample.AmendSampleRawData
import v1.services.{AmendSampleService, _}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendSampleController @Inject()(val authService: EnrolmentsAuthService,
                                      val lookupService: MtdIdLookupService,
                                      appConfig: AppConfig,
                                      requestParser: AmendSampleRequestParser,
                                      service: AmendSampleService,
                                      auditService: AuditService,
                                      cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AuthorisedController(cc) with BaseController with Logging with AmendHateoasBodies {

  implicit val endpointLogContext: EndpointLogContext = EndpointLogContext(
    controllerName = "AmendSampleController",
    endpointName = "amendSample"
  )

  def amendSample(nino: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>

      val rawData = AmendSampleRawData(
        nino = nino,
        taxYear = taxYear,
        body = request.body
      )

      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          serviceResponse <- EitherT(service.amendSample(parsedRequest))
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          auditSubmission(createAuditDetails(
              rawData = rawData,
              statusCode = OK,
              correlationId = serviceResponse.correlationId,
              userDetails = request.userDetails
            ))

          Ok(amendSampleHateoasBody(appConfig, nino, taxYear))
            .withApiHeaders(serviceResponse.correlationId)
            .as(MimeTypes.JSON)
        }

      result.leftMap { errorWrapper =>
        val correlationId = getCorrelationId(errorWrapper)
        val result = errorResult(errorWrapper).withApiHeaders(correlationId)

        auditSubmission(createAuditDetails(
            rawData = rawData,
            statusCode = result.header.status,
            correlationId = correlationId,
            userDetails = request.userDetails,
            errorWrapper = Some(errorWrapper)
          ))

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case RuleIncorrectOrEmptyBodyError | BadRequestError |
           TaxYearFormatError | RuleTaxYearNotSupportedError |
           NinoFormatError | RuleTaxYearRangeInvalidError
      => BadRequest(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }

  private def createAuditDetails(rawData: AmendSampleRawData,
                                 statusCode: Int,
                                 correlationId: String,
                                 userDetails: UserDetails,
                                 errorWrapper: Option[ErrorWrapper] = None): SampleAuditDetail = {

    val response: SampleAuditResponse = errorWrapper
      .map(wrapper => SampleAuditResponse(statusCode, Some(wrapper.auditErrors)))
      .getOrElse(SampleAuditResponse(statusCode, None))

    SampleAuditDetail(
      userType = userDetails.userType,
      agentReferenceNumber = userDetails.agentReferenceNumber,
      nino = rawData.nino,
      taxYear = rawData.taxYear,
      `X-CorrelationId` = correlationId,
      response = response
    )
  }

  private def auditSubmission(details: SampleAuditDetail)(implicit hc: HeaderCarrier,
                                                          ec: ExecutionContext): Future[AuditResult] = {

    val event: AuditEvent[SampleAuditDetail] = AuditEvent(
      auditType = "sampleAuditType",
      transactionName = "sample-transaction-type",
      detail = details
    )

    auditService.auditEvent(event)
  }
}
