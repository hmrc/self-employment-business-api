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

package routing

import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json.Json
import play.api.test.FakeRequest
import support.UnitSpec

class VersionSpec extends UnitSpec {

  "Versions" when {
    "retrieved from a request header" should {
      "return VersionNotFound for unrecognised version" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "application/vnd.hmrc.5.0+json"))) shouldBe Left(VersionNotFound)
      }

      "return InvalidHeader if the Accept header value is invalid" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "application/XYZ.2.0+json"))) shouldBe Left(InvalidHeader)
      }

      "return Version for valid header" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "application/vnd.hmrc.3.0+json"))) shouldBe Right(Version3)
      }
    }
  }

  "serialized to Json" must {
    "return the expected Json output" in {
      val version: Version = Version4
      val expected         = Json.parse(""" "4.0" """)
      val result           = Json.toJson(version)
      result shouldBe expected
    }
  }

}
