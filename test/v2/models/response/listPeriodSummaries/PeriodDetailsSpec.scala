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

package v2.models.response.listPeriodSummaries

import play.api.libs.json.Json
import shared.utils.UnitSpec
class PeriodDetailsSpec extends UnitSpec {

  private val model =
    PeriodDetails(
      periodId = "2020-01-01_2020-02-02",
      periodStartDate = "2020-01-01",
      periodEndDate = "2020-02-02"
//      periodCreationDate = Some("2020-01-02") // To be reinstated, see MTDSA-15595
    )

  "reads" should {

    "read from downstream json" in {
      Json
        .parse(
          """
          |{
          |   "transactionReference": "1111111111",
          |   "from": "2020-01-01",
          |   "to": "2020-02-02"
          |}
          |""".stripMargin
        )
        .as[PeriodDetails] shouldBe model
    }
  }

  "writes" should {

    "write to mtd json" in {
      Json.toJson(model) shouldBe Json.parse(
        """
          |{
          |   "periodId": "2020-01-01_2020-02-02",
          |   "periodStartDate": "2020-01-01",
          |   "periodEndDate": "2020-02-02"
          |}
          |""".stripMargin
      )
    }
  }

}
