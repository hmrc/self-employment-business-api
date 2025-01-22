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

package v5.createAmendAnnualSubmission.def2

import api.models.domain.ex.MtdNicExemption
import api.models.errors.Class4ExemptionReasonFormatError
import cats.data.Validated
import cats.implicits._
import config.SeBusinessConfig
import play.api.libs.json._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveNonEmptyJsonObject, ResolveTaxYearMinimum}
import shared.models.errors.MtdError
import v5.createAmendAnnualSubmission.def2.Def2_CreateAmendAnnualSubmissionRulesValidator.validateBusinessRules
import v5.createAmendAnnualSubmission.def2.request.Def2_CreateAmendAnnualSubmissionRequestBody
import v5.createAmendAnnualSubmission.model.request.{CreateAmendAnnualSubmissionRequestData, Def2_CreateAmendAnnualSubmissionRequestData}

class Def2_CreateAmendAnnualSubmissionValidator(
    nino: String,
    businessId: String,
    taxYear: String,
    body: JsValue
) extends Validator[CreateAmendAnnualSubmissionRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def2_CreateAmendAnnualSubmissionRequestBody]()

  private val resolveTaxYear =
    ResolveTaxYearMinimum(minimumTaxYear = SeBusinessConfig.minimumTaxYear)

  def validate: Validated[Seq[MtdError], CreateAmendAnnualSubmissionRequestData] =
    validateClass4ExemptionReasonEnum andThen { _ =>
      (
        ResolveNino(nino),
        ResolveBusinessId(businessId),
        resolveTaxYear(taxYear),
        resolveJson(body)
      ).mapN(Def2_CreateAmendAnnualSubmissionRequestData) andThen validateBusinessRules
    }

  private def validateClass4ExemptionReasonEnum: Validated[Seq[MtdError], Unit] = {
    val either = (body \ "nonFinancials" \ "class4NicsExemptionReason").validate[String] match {
      case JsSuccess(class4NicsExemptionReason, _) if MtdNicExemption.parser.isDefinedAt(class4NicsExemptionReason) => Right(())
      case JsSuccess(_, _) => Left(List(Class4ExemptionReasonFormatError))
      case _: JsError      => Right(())
    }

    Validated.fromEither(either)
  }

}
