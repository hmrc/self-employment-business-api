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

package config

import com.google.inject.ImplementedBy
import play.api.Configuration
import shared.config.AppConfig

import javax.inject.Inject

@ImplementedBy(classOf[FeatureSwitchesImpl])
trait FeatureSwitches {
  def isPassDeleteIntentEnabled: Boolean
  def isAllowNegativeExpensesEnabled: Boolean
  def isCl290Enabled: Boolean
  def isEnabled(key: String): Boolean
  def isReleasedInProduction(feature: String): Boolean
  def isAdjustmentsAdditionalFieldsEnabled: Boolean
  def isDesIf_MigrationEnabled: Boolean
}

case class FeatureSwitchesImpl(featureSwitchConfig: Configuration) extends FeatureSwitches {

  @Inject
  def this(appConfig: AppConfig) = this(appConfig. featureSwitchConfig)

  val isPassDeleteIntentEnabled: Boolean               = isConfigTrue("passDeleteIntentHeader.enabled")
  val isAllowNegativeExpensesEnabled: Boolean          = isConfigTrue("allowNegativeExpenses.enabled")
  val isCl290Enabled: Boolean                          = isConfigTrue("cl290.enabled")
  val isAdjustmentsAdditionalFieldsEnabled: Boolean    = isConfigTrue("adjustmentsAdditionalFields.enabled")
  val isDesIf_MigrationEnabled: Boolean                = isConfigTrue("desIf_Migration.enabled")

  def isEnabled(key: String): Boolean                  = isConfigTrue(key + ".enabled")
  def isReleasedInProduction(feature: String): Boolean = isConfigTrue(feature + ".released-in-production")

  private def isConfigTrue(key: String): Boolean = featureSwitchConfig.getOptional[Boolean](key).getOrElse(true)
}

object FeatureSwitches {
  def apply(appConfig: AppConfig): FeatureSwitches = FeatureSwitchesImpl(appConfig. featureSwitchConfig)
}