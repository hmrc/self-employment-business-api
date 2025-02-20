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

package v5.listPeriodSummaries.def1.model.response

import play.api.libs.json.Json
import shared.config.MockSharedAppConfig
import shared.utils.UnitSpec

class Def1_ListPeriodSummariesResponseSpec extends UnitSpec with MockSharedAppConfig {

  private val model = Def1_ListPeriodSummariesResponse(
    Seq(
      Def1_PeriodDetails(
        periodId = "2019-01-01_2020-01-01",
        periodStartDate = "2019-01-01",
        periodEndDate = "2020-01-01"
        //        periodCreationDate = Some("2020-01-02") // To be reinstated, see MTDSA-15595
      ),
      Def1_PeriodDetails(
        periodId = "2019-01-01_2020-01-01",
        periodStartDate = "2019-01-01",
        periodEndDate = "2020-01-01"
        //        periodCreationDate = Some("2020-01-02") // To be reinstated, see MTDSA-15595
      )
    )
  )

  "reads" should {
    "read from downstream json" in {
      Json
        .parse(
          """
            |{
            |   "periods": [
            |      {
            |           "transactionReference": "32131123131",
            |           "from": "2019-01-01",
            |           "to": "2020-01-01",
            |           "periodCreationDate": "2020-01-02"
            |      },
            |      {
            |           "transactionReference": "3123123123121",
            |           "from": "2019-01-01",
            |           "to": "2020-01-01",
            |           "periodCreationDate": "2020-01-02"
            |      }
            |   ]
            |}
    """.stripMargin
        )
        .as[Def1_ListPeriodSummariesResponse[Def1_PeriodDetails]] shouldBe model
    }
  }

  "writes" should {
    "write to mtd json" in {
      Json.toJson(model) shouldBe Json.parse(
        """
          |{
          |   "periods": [
          |     {
          |         "periodId": "2019-01-01_2020-01-01",
          |         "periodStartDate": "2019-01-01",
          |         "periodEndDate": "2020-01-01"
          |     },
          |     {
          |         "periodId": "2019-01-01_2020-01-01",
          |         "periodStartDate": "2019-01-01",
          |         "periodEndDate": "2020-01-01"
          |     }
          |   ]
          |}
    """.stripMargin
      )
    }
  }

}
