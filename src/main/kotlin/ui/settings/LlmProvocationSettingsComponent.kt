package net.joshuabrandes.ui.settings

import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.jetbrains.rd.util.string.printToString
import io.ktor.client.plugins.logging.DEFAULT
import net.joshuabrandes.llm.LlmProvider
import net.joshuabrandes.utils.CredentialsService
import net.joshuabrandes.utils.LlmProvocationSettings
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel

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
class LlmProvocationSettingsComponent {

    val panel: JPanel
    private val providerComboBox = ComboBox(LlmProvider.entries.toTypedArray())
    private var modelComboBox = ComboBox<LLModel>()
    private val apiKeyField = JBPasswordField()

    // custom url for Ollama and Custom OpenAI compatible apis
    private val providerUrlField = JBTextField()
    // fallback for ollama and openai compatible apis
    private val modelTextField = JBTextField()

    init {
        updateModelOptions()

        // update model options ans maybe show url field when provider changes
        providerComboBox.addActionListener {
            val selectedProvider = providerComboBox.selectedItem as? LlmProvider
            updateModelOptions(selectedProvider)
            if (selectedProvider != null && (selectedProvider == LlmProvider.OLLAMA || selectedProvider == LlmProvider.OPENAI_COMPATIBLE)) {
                providerUrlField.isVisible = true
            } else {
                providerUrlField.isVisible = false
            }
        }

        // update model options when model changes
        modelComboBox.addActionListener {
            val selectedModel = modelComboBox.selectedItem as? LLModel
            LlmProvocationSettings.instance.selectedModel = selectedModel ?: LLModel(LLMProvider.OpenAI, modelTextField.text)
        }

        // update apiKeyField when provider changes
        providerComboBox.addActionListener {
            val selectedProvider = providerComboBox.selectedItem as? LlmProvider
            if (selectedProvider != null) {
                val creds = CredentialsService.getCredentialsByProvider(selectedProvider)
                apiKeyField.text = (creds?.password?.printToString() ?: "") as String?
            }
        }

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("LLM Provider:", providerComboBox)
            .addLabeledComponent("Model:", modelComboBox)
            .addLabeledComponent("Model (custom):", modelTextField, 1, false)
            .addLabeledComponent("API Key:", apiKeyField)
            .addLabeledComponent("API URL (custom):", providerUrlField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    private fun updateModelOptions(provider: LlmProvider? = null) {
        try {
            val models = ProviderModels.getModels(provider)
            modelComboBox.model = DefaultComboBoxModel(models.toTypedArray())
        } catch (ex: NotImplementedError) {
            // fallback for ollama and openai compatible apis
            if (provider != null && (provider == LlmProvider.OLLAMA || provider == LlmProvider.OPENAI_COMPATIBLE)) {
                val creds = CredentialsService.getCredentialsByProvider(provider)
                if (creds != null && !creds.userName.isNullOrBlank() && !creds.password.isNullOrBlank()) {
                    modelTextField.isVisible = false
                    modelTextField.isEnabled = false
                    val models = ProviderModels.getOpenAICompatibleModelsBlocking(
                        baseUrl = creds.userName!!,
                        apiKey = creds.password
                    )
                    modelComboBox.model = DefaultComboBoxModel(models.toTypedArray())
                } else {
                    modelTextField.isVisible = true
                    modelTextField.isEnabled = true
                }
            }
        }
    }
}