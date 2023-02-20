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

import api.controllers.{AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.hateoas.HateoasFactory
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.IdGenerator
import v1.controllers.requestParsers.ListPeriodSummariesRequestParser
import v1.models.request.listPeriodSummaries.ListPeriodSummariesRawData
import v1.models.response.listPeriodSummaries.ListPeriodSummariesHateoasData
import v1.services.ListPeriodSummariesService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListPeriodSummariesController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               parser: ListPeriodSummariesRequestParser,
                                               service: ListPeriodSummariesService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "ListPeriodSummariesController", endpointName = "listSelfEmploymentPeriodSummaries")

  def handleRequest(nino: String, businessId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = ListPeriodSummariesRawData(nino, businessId, taxYear)

      val taxYearHateoas = taxYear match {
        case Some(year) => Some(TaxYear.fromMtd(year))
        case None       => None
      }

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.listPeriodSummaries)
        .withPlainJsonResult()
        .withHateoasResult(hateoasFactory)(ListPeriodSummariesHateoasData(Nino(nino), BusinessId(businessId), taxYearHateoas))

      requestHandler.handleRequest(rawData)
    }

}
