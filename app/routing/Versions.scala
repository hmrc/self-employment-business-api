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
import play.api.libs.json._
import play.api.mvc.RequestHeader

object Version {

  def from(request: RequestHeader, orElse: Version): Version =
    Versions.getFromRequest(request).getOrElse(orElse)

  object VersionWrites extends Writes[Version] {

    def writes(version: Version): JsValue = version.asJson

  }

  object VersionReads extends Reads[Version] {

    /** @param version
      *   expecting a JsString e.g. "1.0"
      */
    override def reads(version: JsValue): JsResult[Version] =
      version
        .validate[String]
        .flatMap(name =>
          Versions.getFrom(name) match {
            case Left(_)        => JsError("Version not recognised")
            case Right(version) => JsSuccess(version)
          })

  }

  implicit val versionFormat: Format[Version] = Format(VersionReads, VersionWrites)
}

sealed trait Version {
  val name: String
  lazy val asJson: JsValue      = Json.toJson(name)
  override def toString: String = name
}

case object Version2 extends Version {
  val name = "2.0"
}

case object Version3 extends Version {
  val name = "3.0"
}

case object Version4 extends Version {
  val name = "4.0"
}

object Versions {

  private val versionsByName: Map[String, Version] = Map(
    Version2.name -> Version2,
    Version3.name -> Version3,
    Version4.name -> Version4
  )

  private val versionRegex = """application/vnd.hmrc.(\d.\d)\+json""".r

  def getFromRequest(request: RequestHeader): Either[GetFromRequestError, Version] =
    for {
      str <- getFrom(request.headers.headers)
      ver <- getFrom(str)
    } yield ver

  def getFrom(headers: Seq[(String, String)]): Either[GetFromRequestError, String] =
    headers.collectFirst { case (ACCEPT, versionRegex(ver)) => ver }.toRight(left = InvalidHeader)

  def getFrom(name: String): Either[GetFromRequestError, Version] =
    versionsByName.get(name).toRight(left = VersionNotFound)

}

sealed trait GetFromRequestError
case object InvalidHeader   extends GetFromRequestError
case object VersionNotFound extends GetFromRequestError
