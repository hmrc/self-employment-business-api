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

import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import utils.Logging
import v1.controllers.requestParsers.ListSelfEmploymentPeriodicRequestParser
import v1.hateoas.HateoasFactory
import v1.models.request.listSEPeriodic.ListSelfEmploymentPeriodicRawData
import v1.services.{ EnrolmentsAuthService, ListSelfEmploymentPeriodicService, MtdIdLookupService }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class ListSelfEmploymentPeriodicController @Inject()(val authService: EnrolmentsAuthService,
                                                     val lookupService: MtdIdLookupService,
                                                     parser: ListSelfEmploymentPeriodicRequestParser,
                                                     service: ListSelfEmploymentPeriodicService,
                                                     hateoasFactory: HateoasFactory,
                                                     cc: ControllerComponents,
                                                     controllerFactory: StandardControllerFactory)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "ListSelfEmploymentPeriodicController", endpointName = "listSelfEmploymentPeriodicUpdate")

  private val controller =
    controllerFactory
      .using(parser, service)
      .withResultCreator(ResultCreator.hateoasListWrapping(hateoasFactory))
      .createController

  def handleRequest(nino: String, businessId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = ListSelfEmploymentPeriodicRawData(nino, businessId)

      controller.handleRequest(rawData)
    }
}
