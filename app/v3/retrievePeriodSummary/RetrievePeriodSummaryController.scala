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

package v3.retrievePeriodSummary

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.config.AppConfig
import shared.controllers.{AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import shared.hateoas.HateoasFactory
import shared.models.domain.TaxYear
import shared.services.{EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.IdGenerator
import v3.retrievePeriodSummary.model.response.RetrievePeriodSummaryHateoasData

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.Try

@Singleton
class RetrievePeriodSummaryController @Inject() (val authService: EnrolmentsAuthService,
                                                 val lookupService: MtdIdLookupService,
                                                 validatorFactory: RetrievePeriodSummaryValidatorFactory,
                                                 service: RetrievePeriodSummaryService,
                                                 hateoasFactory: HateoasFactory,
                                                 cc: ControllerComponents,
                                                 idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc) {

  val endpointName = "retrieve-period-summary"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "RetrievePeriodSummaryController", endpointName = endpointName)

  def handleRequest(nino: String, businessId: String, periodId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, businessId, periodId, taxYear)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.retrievePeriodSummary)
          .withHateoasResultFrom(hateoasFactory) { (parsedRequest, _) =>
            val maybeTaxYear = taxYear.flatMap(t => Try(TaxYear.fromMtd(t)).toOption)
            RetrievePeriodSummaryHateoasData(parsedRequest.nino, parsedRequest.businessId, periodId, maybeTaxYear)
          }

      requestHandler.handleRequest()
    }

}
