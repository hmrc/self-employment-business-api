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

import api.controllers.{AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.hateoas.HateoasFactory
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import utils.IdGenerator
import v2.controllers.validators.AmendAnnualSubmissionValidatorFactory
import v2.models.response.amendSEAnnual.AmendAnnualSubmissionHateoasData
import v2.models.response.amendSEAnnual.AmendAnnualSubmissionResponse.AmendAnnualSubmissionLinksFactory
import v2.services.AmendAnnualSubmissionService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmendAnnualSubmissionController @Inject() (val authService: EnrolmentsAuthService,
                                                 val lookupService: MtdIdLookupService,
                                                 validatorFactory: AmendAnnualSubmissionValidatorFactory,
                                                 service: AmendAnnualSubmissionService,
                                                 hateoasFactory: HateoasFactory,
                                                 cc: ControllerComponents,
                                                 idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "AmendAnnualSubmissionController", endpointName = "amendSelfEmploymentAnnualSubmission")

  def handleRequest(nino: String, businessId: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, businessId, taxYear, request.body)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.amendAnnualSubmission)
        .withHateoasResultFrom(hateoasFactory)((parsedRequest, _) =>
          AmendAnnualSubmissionHateoasData(parsedRequest.nino, parsedRequest.businessId, parsedRequest.taxYear.asMtd))

      requestHandler.handleRequest()
    }

}
