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

import shared.controllers._
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.hateoas.HateoasFactory
import shared.utils.IdGenerator
import v2.controllers.validators.ListPeriodSummariesValidatorFactory
import v2.models.response.listPeriodSummaries.ListPeriodSummariesHateoasData
import v2.services.ListPeriodSummariesService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListPeriodSummariesController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               validatorFactory: ListPeriodSummariesValidatorFactory,
                                               service: ListPeriodSummariesService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "ListPeriodSummariesController", endpointName = "handleRequest")

  def handleRequest(nino: String, businessId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, businessId, taxYear)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.listPeriodSummaries)
        .withResultCreator(ResultCreator.hateoasListWrapping(hateoasFactory)((_, _) =>
          ListPeriodSummariesHateoasData(Nino(nino), BusinessId(businessId), taxYear.map(TaxYear.fromMtd))))

      requestHandler.handleRequest()
    }

}
