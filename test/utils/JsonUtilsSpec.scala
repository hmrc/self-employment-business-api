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

package utils

import play.api.libs.functional.syntax._
import play.api.libs.json.{KeyPathNode, _}
import support.UnitSpec

class JsonUtilsSpec extends UnitSpec with JsonUtils {

  case class Foo(foo1: String, foo2: String)

  object Foo {
    implicit val reads: Reads[Foo] =
      ((__ \ "baz" \ "foo" \ "foo1").read[String] and
        (__ \ "foo2").read[String]) (Foo.apply _)
  }

  case class Bar(p1: String, foo: Option[Foo])

  object Bar extends JsonUtils {
    implicit val reads: Reads[Bar] =
      ((__ \ "b1").read[String] and
        emptyIfNotPresent[Foo](__ \ "baz" \ "foo")) (Bar.apply _)

  }

  case class MatchClass(id: String, name: NameClass)

  object MatchClass {
    implicit val formats: OFormat[MatchClass] = Json.format[MatchClass]
  }

  case class NameClass(fullName: String, firstName: String, lastName: String)

  object NameClass {
    implicit val formats: OFormat[NameClass] = Json.format[NameClass]
  }

  case class NestedDataClass(data: String, matchClass: Option[MatchClass])

  "emptyIfNotPresent" when {
    "required path not present" must {
      "read as None" in {
        Json.parse(
          """
            |{
            |  "b1": "B1",
            |  "foo2": "F2"
            |}
            |""".stripMargin).as[Bar] shouldBe Bar("B1", None)
      }
    }

    "part of required path not present" must {
      "read as None" in {
        Json.parse(
          """
            |{
            |  "b1": "B1",
            |  "baz": {
            |  },
            |  "foo2": "F2"
            |}
            |""".stripMargin).as[Bar] shouldBe Bar("B1", None)
      }
    }

    "no fields of the optional object are present" must {
      "read as None" in {
        Json.parse(
          """
            |{
            |  "b1": "B1"
            |}
            |""".stripMargin).as[Bar] shouldBe Bar("B1", None)
      }
    }

    "required path is present" when {
      "the schema is correct for the target object" must {
        "read it" in {
          Json.parse(
            """
              |{
              |  "b1": "B1",
              |  "baz": {
              |    "foo": {
              |      "foo1": "F1"
              |    }
              |  },
              |  "foo2": "F2"
              |}
              |""".stripMargin).as[Bar] shouldBe Bar("B1", Some(Foo("F1", "F2")))
        }
      }

      "the schema is incorrect for the target object under the parent" must {
        "validate with an error" in {
          // Invalid because required field missing...
          Json.parse(
            """
              |{
              |  "b1": "B1",
              |  "baz": {
              |    "foo": {
              |    }
              |  },
              |  "foo2": "F2"
              |}
              |""".stripMargin).validate[Bar] shouldBe a[JsError]
        }
      }

      "the schema is incorrect for the target object under a field not under the parent" must {
        "validate with an error" in {
          // Invalid because required field missing...
          Json.parse(
            """
              |{
              |  "b1": "B1",
              |  "baz": {
              |    "foo": {
              |      "foo1": "F1"
              |    }
              |  }
              |}
              |""".stripMargin).validate[Bar] shouldBe a[JsError]
        }
      }
    }
  }

  "filteredArrayReads" should {
    val json = Json.parse(
      """
        |[
        |   {
        |     "id" : "1",
        |     "name" : {
        |       "fullName": "John Doe",
        |       "firstName" : "John",
        |       "lastName": "Doe"
        |     }
        |   },
        |   {
        |     "id" : "1",
        |     "name" : {
        |       "fullName": "John Smith",
        |       "firstName" : "John",
        |       "lastName": "Smith"
        |     }
        |   },
        |   {
        |     "id" : "3",
        |     "name" : {
        |       "fullName": "John Smith",
        |       "firstName" : "Jane",
        |       "lastName": "Doe"
        |     }
        |   },
        |   {
        |     "id" : "4",
        |     "invalidName" : "Fake Name"
        |   },
        |   {
        |     "invalidId" : 5,
        |     "name" : "John Doe"
        |   }
        |]
      """.stripMargin)

    "return a sequence of filtered values" when {

      "provided with a matching parameter for multiple values" in {
        filteredArrayReads[MatchClass]("id", "1")
          .reads(json).get shouldBe Seq(
          MatchClass("1", NameClass("John Doe", "John", "Doe")),
          MatchClass("1", NameClass("John Smith", "John", "Smith"))
        )
      }

      "provided with a non-matching parameter value" in {
        filteredArrayReads[MatchClass]("id", "2")
          .reads(json).get shouldBe Seq()
      }

      "provided with a non-matching parameter" in {
        filteredArrayReads[MatchClass]("badId", "1")
          .reads(json).get shouldBe Seq()
      }
    }

    "return a JsError" when {

      "provided with a matching parameter for an invalid json object" in {
        val result = filteredArrayReads[MatchClass]("id", "4").reads(json)

        result shouldBe a[JsError]
      }
    }
  }

  "readNestedNullable" should {

    implicit val reads: Reads[NestedDataClass] = (
      (JsPath \ "data").read[String] and
        (JsPath \ "topLevel" \ "midLevel" \ "bottomLevel").readNestedNullable[MatchClass]
      ) (NestedDataClass.apply _)

    "return Some data" when {

      "provided with Json with valid data" in {
        val json = Json.parse(
          """
            |{
            | "data" : "data",
            | "topLevel" : {
            |   "midLevel" : {
            |     "bottomLevel":
            |       {
            |         "id" : "1",
            |         "name" : {
            |           "fullName": "John Doe",
            |           "firstName" : "John",
            |           "lastName": "Doe"
            |         }
            |       }
            |   }
            | }
            |}
          """.stripMargin)

        json.as[NestedDataClass] shouldBe NestedDataClass("data", Some(MatchClass("1", NameClass("John Doe", "John", "Doe"))))
      }
    }

    "return a None" when {

      "provided with json with a missing path component" in {
        val json = Json.parse(
          """
            |{
            | "data" : "data",
            | "topLevel" : {
            |   "midLevel" : {

            |   }
            | }
            |}
          """.stripMargin)

        json.as[NestedDataClass] shouldBe NestedDataClass("data", None)
      }
    }

    "return a JsError" when {

      "provided with invalid data" in {
        val json = Json.parse(
          """
            |{
            | "data" : "data",
            | "topLevel" : {
            |   "midLevel" : {
            |     "bottomLevel":
            |       {
            |         "id" : "1",
            |         "name" : {
            |           "firstName" : "John",
            |           "lastName": "Doe"
            |         }
            |       }
            |   }
            | }
            |}
          """.stripMargin)

        val result = json.validate[NestedDataClass]
        result shouldBe a[JsError]
        result.asInstanceOf[JsError].errors.head._1 shouldBe JsPath(List(
          KeyPathNode("topLevel"),
          KeyPathNode("midLevel"),
          KeyPathNode("bottomLevel"),
          KeyPathNode("name"),
          KeyPathNode("fullName")
        ))
      }
    }
  }

  "mapEmptySeqToNone" must {
    val reads = __.readNullable[Seq[String]].mapEmptySeqToNone

    "map non-empty sequence to Some(non-empty sequence)" in {
      JsArray(Seq(JsString("value0"), JsString("value1"))).as(reads) shouldBe Some(Seq("value0", "value1"))
    }

    "map empty sequence to None" in {
      JsArray.empty.as(reads) shouldBe None
    }

    "map None to None" in {
      JsNull.as(reads) shouldBe None
    }
  }

  "mapHeadOption" must {
    val reads = __.readNullable[Seq[String]].mapHeadOption

    "map non-empty sequence to Some(1st element)" in {
      JsArray(Seq(JsString("value0"), JsString("value1"))).as(reads) shouldBe Some("value0")
    }

    "map empty sequence to None" in {
      JsArray.empty.as(reads) shouldBe None
    }

    "map None to None" in {
      JsNull.as(reads) shouldBe None
    }
  }
}
