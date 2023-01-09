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

package v1.controllers

import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.Helpers.stubControllerComponents
import play.api.test.{FakeRequest, ResultExtractors}
import support.UnitSpec
import v1.models.hateoas.Link
import v1.models.hateoas.Method.GET

class ControllerBaseSpec extends UnitSpec with Status with MimeTypes with HeaderNames with ResultExtractors {

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val cc: ControllerComponents = stubControllerComponents()

  lazy val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withHeaders(
    HeaderNames.AUTHORIZATION -> "Bearer Token"
  )

  lazy val fakeDeleteRequest: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withHeaders(
    HeaderNames.AUTHORIZATION -> "Bearer Token"
  )

  val testHateoasLinks: Seq[Link] =
    Seq(Link(href = "/some/link", method = GET, rel = "someRel"))

  val testHateoasLinksJson: JsObject = Json
    .parse("""{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin)
    .as[JsObject]

  def fakePostRequest[T](body: T): FakeRequest[T] = fakeRequest.withBody(body)

  def fakePutRequest[T](body: T): FakeRequest[T] = fakeRequest.withBody(body)

}
