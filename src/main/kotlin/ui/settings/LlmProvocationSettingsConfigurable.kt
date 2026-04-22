package net.joshuabrandes.ui.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import net.joshuabrandes.utils.CredentialsService
import net.joshuabrandes.utils.LlmProvocationSettings
import javax.swing.JComponent

/*
 * Copyright 2026 Joshua Brandes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class LlmProvocationSettingsConfigurable : Configurable {

    private var settingsComponent: LlmProvocationSettingsComponent? = null
    override fun getDisplayName(): @NlsContexts.ConfigurableName String? = "AI Assistant Provocations"

    override fun createComponent(): JComponent? {
        settingsComponent = LlmProvocationSettingsComponent()
        return settingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val settings = LlmProvocationSettings.instance
        val component = settingsComponent ?: return false
        val storedKey = CredentialsService.getCredentialsByProvider(settings.selectedProvider)
            ?.getPasswordAsString() ?: ""

        return component.selectedProvider != settings.selectedProvider
                || component.selectedModel != settings.selectedModel
                || component.apiKey != storedKey
    }

    override fun apply() {
        val settings = LlmProvocationSettings.instance
        val component = settingsComponent ?: return

        settings.selectedProvider = component.selectedProvider
        settings.selectedModel = component.selectedModel

        val key = component.apiKey
        if (key.isNotBlank()) {
            val credKey = CredentialsService.getCredentialKeyByProvider(component.selectedProvider)
            if (credKey != null) {
                CredentialsService.saveKeyAsPassword(credKey, key)
            }
        }
    }

    override fun reset() {
        val settings = LlmProvocationSettings.instance
        val component = settingsComponent ?: return

        component.selectedProvider = settings.selectedProvider
        component.selectedModel = settings.selectedModel
        component.modelName = settings.selectedModel.id
        val storedKey = CredentialsService.getCredentialsByProvider(settings.selectedProvider)
            ?.getPasswordAsString() ?: ""
        component.apiKey = storedKey
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
