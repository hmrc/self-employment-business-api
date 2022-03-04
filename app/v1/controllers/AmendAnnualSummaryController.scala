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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents, Result}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.AmendSelfEmploymentAnnualSummaryRequestParser
import v1.hateoas.{HateoasFactory, HateoasLinksFactory}
import v1.models.errors.ErrorWrapper.WithCode
import v1.models.errors._
import v1.models.hateoas.HateoasDataBuilder
import v1.models.request.amendSEAnnual.{AmendAnnualSummaryRawData, AmendAnnualSummaryRequest}
import v1.models.response.amendSEAnnual.{AmendAnnualSummaryHateoasData, AmendAnnualSummaryResponse}
import v1.services.{AmendAnnualSummaryService, EnrolmentsAuthService, MtdIdLookupService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmendAnnualSummaryController @Inject()(val authService: EnrolmentsAuthService,
                                             val lookupService: MtdIdLookupService,
                                             val parser: AmendSelfEmploymentAnnualSummaryRequestParser,
                                             val service: AmendAnnualSummaryService,
                                             val hateoasFactory: HateoasFactory,
                                             cc: ControllerComponents,
                                             val idGenerator: IdGenerator)(implicit val ec: ExecutionContext)
  extends AuthorisedController(cc)
    with StandardController with SimpleHateoasWrapping
    with Logging {

  type InputRaw = AmendAnnualSummaryRawData
  type Input = AmendAnnualSummaryRequest
  type Output = AmendAnnualSummaryResponse
  type HData = AmendAnnualSummaryHateoasData

  // TODO often raw input == hateoas data (maybe POSTs will return an id that is also reqd) but can we do away with
  // separate InputRaw and HData types (and hence two typeclass instances) in most cases?
  override val hateoasLinksFactory: HateoasLinksFactory[Output, HData] = implicitly
  override val hateoasDataBuilder: HateoasDataBuilder[InputRaw, HData] = implicitly

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendAnnualSummaryController", endpointName = "amendAnnualSummary")

  def handleRequest(nino: String, businessId: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      val rawData = AmendAnnualSummaryRawData(nino, businessId, taxYear, request.body)

      doHandleRequest(rawData)
    }

  // TODO remove this unless there are controller specific errors not handled by StandardController...
  override protected def errorResultPF(implicit endpointLogContext: EndpointLogContext): PartialFunction[ErrorWrapper, Result] = {
    case errorWrapper@WithCode(ValueFormatError.code) => BadRequest(Json.toJson(errorWrapper))
  }
}
