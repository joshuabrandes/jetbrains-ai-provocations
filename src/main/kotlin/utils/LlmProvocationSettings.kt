package net.joshuabrandes.utils

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.llm.LLModel
import com.intellij.openapi.components.*
import net.joshuabrandes.llm.LlmProvider

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
@State(
    name = "LlmProvocationSettings",
    storages = [Storage("llmProvocationSettings.xml")]
)
@Service(Service.Level.APP)
class LlmProvocationSettings : PersistentStateComponent<LlmProvocationSettings.State> {

    data class State(
        var selectedProvider: LlmProvider = LlmProvider.ANTHROPIC,
        var selectedModel: LLModel = AnthropicModels.Sonnet_4_6
    )

    private var persistedState = State()

    var selectedProvider: LlmProvider
        get() = persistedState.selectedProvider
        set(value) {
            persistedState.selectedProvider = value
        }

    var selectedModel: LLModel
        get() = persistedState.selectedModel
        set(value) {
            persistedState.selectedModel = value
        }

    override fun getState(): LlmProvocationSettings.State {
        return persistedState
    }

    override fun loadState(p0: LlmProvocationSettings.State) {
        persistedState = p0
    }

    companion object {
        val instance: LlmProvocationSettings
            get() = service()
    }
}
