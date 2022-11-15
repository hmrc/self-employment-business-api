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

import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, ControllerComponents }
import utils.Logging
import v1.controllers.requestParsers.AmendSelfEmploymentAnnualSummaryRequestParser
import v1.hateoas.HateoasFactory
import v1.models.errors.ErrorWrapper.WithCode
import v1.models.errors.ValueFormatError
import v1.models.request.amendSEAnnual.{ AmendAnnualSummaryRawData, AmendAnnualSummaryRequest }
import v1.services.{ AmendAnnualSummaryService, EnrolmentsAuthService, MtdIdLookupService }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class AmendAnnualSummaryController @Inject()(val authService: EnrolmentsAuthService,
                                             val lookupService: MtdIdLookupService,
                                             parser: AmendSelfEmploymentAnnualSummaryRequestParser,
                                             service: AmendAnnualSummaryService,
                                             hateoasFactory: HateoasFactory,
                                             cc: ControllerComponents,
                                             requestHandlerFactory: RequestHandlerFactory)(implicit val ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendAnnualSummaryController", endpointName = "amendAnnualSummary")

  private val requestHandler =
    requestHandlerFactory
      .withParser(parser)
      .withService { request: AmendAnnualSummaryRequest => implicit ctx: RequestContext =>
        service.amend(request)
      }
      .withErrorHandling {
        case errorWrapper @ WithCode(ValueFormatError.code) => BadRequest(Json.toJson(errorWrapper))
      }
      .withResultCreator(ResultCreator.hateoasWrapping(hateoasFactory))
      .createRequestHandler

  def handleRequest(nino: String, businessId: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      val rawData = AmendAnnualSummaryRawData(nino, businessId, taxYear, request.body)

      requestHandler.handleRequest(rawData)
    }
}
