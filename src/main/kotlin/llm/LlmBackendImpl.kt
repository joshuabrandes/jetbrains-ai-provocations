package net.joshuabrandes.llm

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.streaming.StreamFrame
import com.intellij.credentialStore.Credentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.joshuabrandes.model.Provocation
import net.joshuabrandes.model.ToolSession
import net.joshuabrandes.utils.CredentialsService
import net.joshuabrandes.utils.LlmProvocationSettings
import org.eclipse.aether.util.concurrency.ExecutorUtils.executor
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
class LlmBackendImpl : LLMBackend {

    @OptIn(ExperimentalTime::class)
    private val BASE_PROMPT = prompt("base-prompt") {
        system("You are a helpful coding assistant.")
    }

    private val providerSettings = LlmProvocationSettings.instance
    private val clientFactory = LlmClientFactory()

    private fun getCredentials(): Credentials =
        CredentialsService.getCredentialsByProvider(providerSettings.selectedProvider)
            ?: throw Exception("No credentials found for provider ${providerSettings.selectedProvider}")

    private fun buildProviderCredentials(provider: LlmProvider, credentials: Credentials): LlmProviderCredentials {
        val apiKey = credentials.password?.toString()
        val url = credentials.userName

        return when(provider) {
            LlmProvider.ANTHROPIC -> LlmProviderCredentials(anthropicKey = apiKey)
            LlmProvider.OPENAI -> LlmProviderCredentials(openaiKey = apiKey)
            LlmProvider.GOOGLE -> LlmProviderCredentials(googleApiKey = apiKey)
            LlmProvider.OLLAMA -> LlmProviderCredentials(ollamaBaseUrl = url)
            LlmProvider.OPENAI_COMPATIBLE -> LlmProviderCredentials(
                compatibleKey = apiKey,
                compatibleBaseUrl = url
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun generateCode(session: ToolSession): Flow<String> {
        val provider = providerSettings.selectedProvider
        val model = providerSettings.selectedModel
        val credentials = getCredentials()
        val providerCredentials = buildProviderCredentials(provider, credentials)
        val executor = clientFactory.createExecutor(provider, providerCredentials)
        val prompt = prompt(BASE_PROMPT) {
            user(Prompt.codeGeneration(session))
        }

        return flow {
            val deltaIndexes = mutableSetOf<Int?>()
            executor.executeStreaming(prompt, model).collect { chunk ->
                when (chunk) {
                    is StreamFrame.TextDelta -> {
                        deltaIndexes += chunk.index
                        emit(chunk.text)
                    }
                    is StreamFrame.TextComplete -> {
                        if (chunk.index !in deltaIndexes) {
                            emit(chunk.text)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    override suspend fun generateProvocations(session: ToolSession): List<Provocation> {
        TODO("Not yet implemented")
    }
}
