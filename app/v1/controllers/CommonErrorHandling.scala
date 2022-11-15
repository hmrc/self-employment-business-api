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

package v1.controllers

import play.api.mvc.Result
import v1.models.errors.ErrorWrapper

trait CommonErrorHandlingComponent {
  def commonErrorHandling: CommonErrorHandling
}

trait ApiCommonErrorHandlingComponent extends CommonErrorHandlingComponent {
  override val commonErrorHandling: CommonErrorHandling = ApiCommonErrorHandling
}

/**
  * Defines default error handling that can be used across all endpoints
  */
trait CommonErrorHandling {
  def errorResultPF: PartialFunction[ErrorWrapper, Result]
}
