package net.joshuabrandes.model

import java.util.UUID
import kotlin.uuid.Uuid

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
data class ToolSession(
    val id: String = UUID.randomUUID().toString(),
    val intent: String,
    val mode: AssistanceMode,
    val codeContext: CodeContext,
    var generatedCode: String? = null,
    var insertedAtOffset: Int? = null,
    val provocations: MutableList<Provocation> = mutableListOf()
) {
    fun addProvocation(provocation: Provocation) {
        provocations.add(provocation)
    }

    fun acknowledgeProvocation(provocation: Provocation) {
        provocations.find { it == provocation }?.acknowledged = true
    }

    fun acknowledgeAllProvocations() {
        provocations.forEach { it.acknowledged = true }
    }

    fun getUnacknowledgedProvocations(): List<Provocation> {
        return provocations.filter { !it.acknowledged }
    }
}
