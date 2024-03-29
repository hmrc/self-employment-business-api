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

package api.hateoas

object RelType {
  val SELF = "self"

  val AMEND_ANNUAL_SUBMISSION_REL  = "create-and-amend-self-employment-annual-submission"
  val DELETE_ANNUAL_SUBMISSION_REL = "delete-self-employment-annual-submission"

  val CREATE_PERIOD_SUMMARY_REL = "create-self-employment-period-summary"
  val AMEND_PERIOD_SUMMARY_REL  = "amend-self-employment-period-summary"
  val LIST_PERIOD_SUMMARIES_REL = "list-self-employment-period-summaries"

}
