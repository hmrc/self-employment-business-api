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

package v1.fixtures

import play.api.libs.json.{JsObject, JsValue, Json}

object RetrieveSampleControllerFixture {

  val desJson: JsValue = Json.parse(
    """
      |{
      |  "historicalIncomeSubmissions": [
      |    {
      |      "itemId": "AAA123",
      |      "submittedAmount": 200.11,
      |      "itemDetail": {
      |        "taxableAmount": 100.14
      |      },
      |      "typeOfItem": "Type1",
      |      "taxYear": "2019",
      |      "isFinalised": true
      |    }
      |  ],
      |  "currentIncomeSubmission": {
      |    "dateSubmitted": "01-01-2019",
      |    "submittedItems": {
      |      "income": [
      |        {
      |          "itemId": "AAA123",
      |          "submittedAmount": 200.11,
      |          "itemDetail": {
      |            "taxableAmount": 100.14
      |          },
      |          "typeOfItem": "Type1",
      |          "taxYear": "2019",
      |          "isFinalised": true
      |        },
      |        {
      |          "itemId": "AAA123",
      |          "submittedAmount": 200.11,
      |          "itemDetail": {
      |            "taxableAmount": 100.14
      |          },
      |          "typeOfItem": "Type2",
      |          "taxYear": "2019",
      |          "isFinalised": true
      |        }
      |      ]
      |    }
      |  },
      |  "totalCharitableContribution": {
      |    "requestedItemId": "anId1",
      |    "typeOfItem": "Type1",
      |    "paymentAmount": 300.54
      |  },
      |  "broughtForwardSubmissions": [
      |    {
      |      "requestedItemId": "anId2",
      |      "typeOfItem": "Type4",
      |      "paymentAmount": 400.54
      |    },
      |    {
      |      "requestedItemId": "anId",
      |      "typeOfItem": "Type2",
      |      "paymentAmount": 300.54
      |    }
      |  ]
      |}
          """.stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "availableCharitableDeduction": {
      |    "deductibleAmount": 300.54,
      |    "itemIdentifier": "anId1",
      |    "itemType": "One"
      |  },
      |  "broughtForwardLosses": [
      |    {
      |      "deductibleAmount": 400.54,
      |      "itemIdentifier": "anId2",
      |      "itemType": "Four"
      |    }
      |  ],
      |  "completedItems": [
      |    {
      |      "declaredAmount": 200.11,
      |      "finalised": true,
      |      "id": "AAA123",
      |      "itemType": "One",
      |      "taxableAmount": 100.14,
      |      "taxYear": "2018-19"
      |    }
      |  ],
      |  "taxableForeignIncome": {
      |    "dateSubmitted": "01-01-2019",
      |    "submissionItem": {
      |        "declaredAmount": 200.11,
      |        "finalised": true,
      |        "id": "AAA123",
      |        "itemType": "One",
      |        "taxableAmount": 100.14,
      |        "taxYear": "2018-19"
      |    }
      |  }
      |}
    """.stripMargin
  )

  def mtdResponseWithHateoas(nino: String, taxYear: String): JsObject = mtdJson.as[JsObject] ++ Json.parse(
    s"""
       |{
       |   "links":[
       |      {
       |         "href":"/mtd/template/sample/$nino/$taxYear",
       |         "method":"PUT",
       |         "rel":"amend-sample-rel"
       |      },
       |      {
       |         "href":"/mtd/template/sample/$nino/$taxYear",
       |         "method":"GET",
       |         "rel":"self"
       |      },
       |      {
       |         "href":"/mtd/template/sample/$nino/$taxYear",
       |         "method":"DELETE",
       |         "rel":"delete-sample-rel"
       |      }
       |   ]
       |}
    """.stripMargin
  ).as[JsObject]
}
