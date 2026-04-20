package net.joshuabrandes.llm

import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
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
class LlmClientFactory {

    @OptIn(ExperimentalTime::class)
    fun createExecutor(provider: LlmProvider, credentials: LlmProviderCredentials): PromptExecutor {
        return when (provider) {
            LlmProvider.ANTHROPIC -> {
                require(!credentials.anthropicKey.isNullOrBlank()) {
                    "Anthropic API key is required for Anthropic provider"
                }

                simpleAnthropicExecutor(credentials.anthropicKey)
            }

            LlmProvider.OPENAI -> {
                require(!credentials.openaiKey.isNullOrBlank()) {
                    "OpenAI API key is required for OpenAI provider"
                }

                simpleOpenAIExecutor(credentials.openaiKey)
            }

            LlmProvider.GOOGLE -> {
                require(!credentials.googleApiKey.isNullOrBlank()) {
                    "Google API key is required for Google provider"
                }

                simpleGoogleAIExecutor(credentials.googleApiKey)
            }

            LlmProvider.OLLAMA -> {
                require(!credentials.ollamaBaseUrl.isNullOrBlank()) {
                    "Ollama URL is required for Ollama provider"
                }

                simpleOllamaAIExecutor(credentials.ollamaBaseUrl)
            }

            LlmProvider.OPENAI_COMPATIBLE -> {
                require(!credentials.compatibleKey.isNullOrBlank()) {
                    "Compatible API key is required for OpenAI-compatible provider"
                }

                require(!credentials.compatibleBaseUrl.isNullOrBlank()) {
                    "Compatible API URL is required for OpenAI-compatible provider"
                }

                val settings = OpenAIClientSettings(
                    baseUrl = credentials.compatibleBaseUrl,
                )

                MultiLLMPromptExecutor(
                    OpenAILLMClient(
                        settings = settings,
                        apiKey = credentials.compatibleKey
                    )
                )
            }
        }
    }
}