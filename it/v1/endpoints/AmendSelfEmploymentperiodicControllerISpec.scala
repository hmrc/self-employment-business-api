/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.endpoints

import play.api.libs.json.Json
import support.IntegrationBaseSpec

class AmendSelfEmploymentPeriodicControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino = "AA123456A"
    val businessId = "XAIS12345678910"
    val periodId = "2019-01-01_2020-01-01"

    val requestJson = Json.parse(
      """
        |{
        |    "incomes": {
        |        "turnover": {
        |            "amount": 172.89
        |        },
        |        "other": {
        |            "amount": 634.14
        |        }
        |    },
        |    "consolidatedExpenses": {
        |        "consolidatedExpenses": 647.89
        |    }
        |}
        |""".stripMargin)

    val responseJson = Json.parse(
      s"""
         |{
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "PUT",
         |      "rel": "amend-periodic-update"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |    }
         |  ]
         |}
         |""".stripMargin)

    def uri: String = s"/$nino/$businessId/periodic/$periodId"

    def desUri: String = s"income-store/nino/$nino/self-employments/$businessId/periodic-summaries$periodId"


  }
}
