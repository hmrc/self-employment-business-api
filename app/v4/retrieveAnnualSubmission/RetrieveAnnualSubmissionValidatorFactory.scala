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

package v4.retrieveAnnualSubmission

import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v4.retrieveAnnualSubmission.RetrieveAnnualSubmissionSchema.{Def1, Def2, Def3}
import v4.retrieveAnnualSubmission.def1.Def1_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.def2.Def2_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.def3.Def3_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class RetrieveAnnualSubmissionValidatorFactory @Inject() {

  def validator(nino: String, businessId: String, taxYear: String): Validator[RetrieveAnnualSubmissionRequestData] =
    RetrieveAnnualSubmissionSchema.schemaFor(taxYear) match {
      case Valid(Def1)                    => new Def1_RetrieveAnnualSubmissionValidator(nino, businessId, taxYear)
      case Valid(Def2)                    => new Def2_RetrieveAnnualSubmissionValidator(nino, businessId, taxYear)
      case Valid(Def3)                    => new Def3_RetrieveAnnualSubmissionValidator(nino, businessId, taxYear)
      case Invalid(errors: Seq[MtdError]) => Validator.returningErrors(errors)
    }

}
