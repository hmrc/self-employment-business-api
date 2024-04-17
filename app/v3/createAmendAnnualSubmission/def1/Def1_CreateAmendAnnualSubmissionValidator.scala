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

package v3.createAmendAnnualSubmission.def1

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{DetailedResolveTaxYear, ResolveBusinessId, ResolveNino, ResolveNonEmptyJsonObject}
import api.models.domain.TaxYear
import api.models.domain.ex.MtdNicExemption
import api.models.errors.{Class4ExemptionReasonFormatError, MtdError, RuleIncorrectOrEmptyBodyError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json._
import v3.createAmendAnnualSubmission.def1.Def1_CreateAmendAnnualSubmissionRulesValidator.validateBusinessRules
import v3.createAmendAnnualSubmission.def1.model.request.Def1_CreateAmendAnnualSubmissionRequestBody
import v3.createAmendAnnualSubmission.model.request.{CreateAmendAnnualSubmissionRequestData, Def1_CreateAmendAnnualSubmissionRequestData}

import scala.annotation.nowarn

class Def1_CreateAmendAnnualSubmissionValidator(nino: String, businessId: String, taxYear: String, body: JsValue, appConfig: AppConfig)
    extends Validator[CreateAmendAnnualSubmissionRequestData] {

  lazy private val featureSwitches = FeatureSwitches(appConfig)

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_CreateAmendAnnualSubmissionRequestBody]()

  private val resolveTaxYear =
    DetailedResolveTaxYear(maybeMinimumTaxYear = Some(TaxYear.minimumTaxYear.year))

  def validate: Validated[Seq[MtdError], CreateAmendAnnualSubmissionRequestData] =
    validateClass4ExemptionReasonEnum andThen { _ =>
      (
        ResolveNino(nino),
        ResolveBusinessId(businessId),
        resolveTaxYear(taxYear),
        resolveJson(body)
      ).mapN(Def1_CreateAmendAnnualSubmissionRequestData) andThen validateBusinessRules andThen validateAdjustmentsAdditionalFields
    }

  private def validateAdjustmentsAdditionalFields(
      parsed: Def1_CreateAmendAnnualSubmissionRequestData): Validated[Seq[MtdError], Def1_CreateAmendAnnualSubmissionRequestData] =
    (for {
      adjustments <- parsed.body.adjustments if !featureSwitches.isAdjustmentsAdditionalFieldsEnabled
      _           <- adjustments.transitionProfitAmount
      _           <- adjustments.transitionProfitAccelerationAmount
    } yield {
      Invalid(
        List(
          RuleIncorrectOrEmptyBodyError.withPath("/adjustments/transitionProfitAmount"),
          RuleIncorrectOrEmptyBodyError.withPath("/adjustments/transitionProfitAccelerationAmount")
        ))
    }).getOrElse(Valid(parsed))

  private def validateClass4ExemptionReasonEnum: Validated[Seq[MtdError], Unit] = {
    val either = (body \ "nonFinancials" \ "class4NicsExemptionReason").validate[String] match {
      case JsSuccess(class4NicsExemptionReason, _) if MtdNicExemption.parser.isDefinedAt(class4NicsExemptionReason) => Right(())
      case JsSuccess(_, _) => Left(List(Class4ExemptionReasonFormatError))
      case _: JsError      => Right(())
    }

    Validated.fromEither(either)
  }

}
