package net.joshuabrandes.ui.settings

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.llm.LLModel
import com.intellij.credentialStore.OneTimeString
import com.jetbrains.rd.util.string.printToString
import kotlinx.coroutines.runBlocking
import net.joshuabrandes.llm.LlmProvider
import kotlin.time.ExperimentalTime

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
object ProviderModels {

    fun getModels(provider: LlmProvider?): List<LLModel> {
        return when (provider) {
            null -> emptyList()
            LlmProvider.ANTHROPIC -> AnthropicModels.models
            LlmProvider.OPENAI -> OpenAIModels.models
            LlmProvider.GOOGLE -> GoogleModels.models
            LlmProvider.OLLAMA -> OllamaModels.models
            LlmProvider.OPENAI_COMPATIBLE -> {
                throw NotImplementedError("Call getOpenAICompatibleModelsBlocking instead")
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun getOpenAICompatibleModels(
        baseUrl: String,
        apiKey: String
    ): List<LLModel> {
        require(baseUrl.isNotBlank()) { "baseUrl is required for OpenAI-compatible provider" }
        require(apiKey.isNotBlank()) { "apiKey is required for OpenAI-compatible provider" }

        val settings = OpenAIClientSettings(baseUrl = baseUrl.trimEnd('/'))

        val client = OpenAILLMClient(
            settings = settings,
            apiKey = apiKey
        )

        return client.models()
    }

    fun getOpenAICompatibleModelsBlocking(
        baseUrl: String,
        apiKey: OneTimeString?
    ): List<LLModel> = runBlocking {
        getOpenAICompatibleModels(baseUrl, apiKey.printToString())
    }
}