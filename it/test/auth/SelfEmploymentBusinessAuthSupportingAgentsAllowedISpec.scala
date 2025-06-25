/*
 * Copyright 2022 HM Revenue & Customs
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

package auth

import play.api.libs.json.JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import shared.auth.AuthSupportingAgentsAllowedISpec
import shared.services.DownstreamStub

class SelfEmploymentBusinessAuthSupportingAgentsAllowedISpec extends AuthSupportingAgentsAllowedISpec {

  override val callingApiVersion = "3.0"
  val taxYear                    = "2023"
  val incomeSourceId             = "XAIS12345678910"

  override val supportingAgentsAllowedEndpoint = "create-amend-annual-submission"

  override val mtdUrl = s"/$nino/$incomeSourceId/annual/2022-23"

  override val downstreamHttpMethod: DownstreamStub.HTTPMethod = DownstreamStub.GET

  override def sendMtdRequest(request: WSRequest): WSResponse = await(request.get())

  override val downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$incomeSourceId/annual-summaries/$taxYear"

  override val maybeDownstreamResponseJson: Option[JsValue] = None

}
