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
import v1.controllers.requestParsers.CreateSelfEmploymentPeriodicRequestParser
import v1.hateoas.HateoasFactory
import v1.models.errors.ErrorWrapper.WithCode
import v1.models.errors._
import v1.models.request.createSEPeriodic.CreateSelfEmploymentPeriodicRawData
import v1.models.response.createSEPeriodic.CreateSelfEmploymentPeriodicResponse.LinksFactory
import v1.services.{ CreateSelfEmploymentPeriodicService, EnrolmentsAuthService, MtdIdLookupService }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class CreateSelfEmploymentPeriodicController @Inject()(val authService: EnrolmentsAuthService,
                                                       val lookupService: MtdIdLookupService,
                                                       parser: CreateSelfEmploymentPeriodicRequestParser,
                                                       service: CreateSelfEmploymentPeriodicService,
                                                       hateoasFactory: HateoasFactory,
                                                       cc: ControllerComponents,
                                                       controllerFactory: StandardControllerFactory)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "CreateSelfEmploymentPeriodController", endpointName = "createSelfEmploymentPeriodSummary")

  private val controller =
    controllerFactory
      .using(parser, service)
      .withErrorHandling {
        case errorWrapper @ (WithCode(FromDateFormatError.code) | WithCode(ToDateFormatError.code) | WithCode(RuleBothExpensesSuppliedError.code) |
            WithCode(RuleToDateBeforeFromDateError.code) | WithCode(RuleOverlappingPeriod.code) | WithCode(RuleMisalignedPeriod.code) |
            WithCode(RuleNotContiguousPeriod.code) | WithCode(RuleNotAllowedConsolidatedExpenses.code)) =>
          BadRequest(Json.toJson(errorWrapper))
      }
      .withResultCreator(ResultCreator.hateoasWrapping(hateoasFactory))
      .createController

  def handleRequest(nino: String, businessId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      val rawData = CreateSelfEmploymentPeriodicRawData(nino, businessId, request.body)

      controller.handleRequest(rawData)
    }
}
