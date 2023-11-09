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

import api.controllers._
import api.hateoas.HateoasFactory
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.IdGenerator
import v1.controllers.validators.RetrievePeriodSummaryValidatorFactory
import v1.models.response.retrievePeriodSummary.RetrievePeriodSummaryHateoasData
import v1.services.RetrievePeriodSummaryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RetrievePeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                                 val lookupService: MtdIdLookupService,
                                                 validatorFactory: RetrievePeriodSummaryValidatorFactory,
                                                 service: RetrievePeriodSummaryService,
                                                 hateoasFactory: HateoasFactory,
                                                 cc: ControllerComponents,
                                                 idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "RetrievePeriodSummaryController", endpointName = "retrieveSelfEmploymentPeriodicSummary")

  def handleRequest(nino: String, businessId: String, periodId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, businessId, periodId, taxYear)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.retrievePeriodSummary)
        .withHateoasResult(hateoasFactory)(
          RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, taxYear.map(TaxYear.fromMtd)))

      requestHandler.handleRequest()
    }

}
