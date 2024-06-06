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

package api.models.errors

import play.api.http.Status._
import shared.models.errors.MtdError

object RuleBusinessIncomePeriodRestriction
    extends MtdError(
      "RULE_BUSINESS_INCOME_PERIOD_RESTRICTION",
      "For customers with ITSA status 'Annual' or a latent business income source, submission period has to be 6 April to 5 April",
      BAD_REQUEST)
