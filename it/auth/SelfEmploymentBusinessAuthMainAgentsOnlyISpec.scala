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

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import shared.auth.AuthMainAgentsOnlyISpec
import shared.services.DownstreamStub
import v3.createAmendAnnualSubmission.def1.model.request.Def1_CreateAmendAnnualSubmissionFixture

class SelfEmploymentBusinessAuthMainAgentsOnlyISpec extends AuthMainAgentsOnlyISpec  with Def1_CreateAmendAnnualSubmissionFixture{

  override val callingApiVersion = "3.0"

  override val supportingAgentsNotAllowedEndpoint = "create-amend-annual-submission"

  override val mtdUrl = s"/$nino/XAIS12345678910/annual/2023-24"

  override val downstreamHttpMethod: DownstreamStub.HTTPMethod = DownstreamStub.PUT

  override def sendMtdRequest(request: WSRequest): WSResponse = await(request.put(createAmendAnnualSubmissionRequestBodyMtdJson()))

  override val downstreamUri: String = s"/income-tax/23-24/$nino/self-employments/XAIS12345678910/annual-summaries"

  override val maybeDownstreamResponseJson: Option[JsValue] = Some(Json.parse("""{"transactionReference": "ignored"}"""))

}
