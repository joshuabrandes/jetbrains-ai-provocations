package net.joshuabrandes.utils

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
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
object CredentialsService {

    const val KEY_ANTHROPIC = "anthropic.api.key"
    const val KEY_OPENAI = "openai.api.key"
    const val KEY_GOOGLE = "google.api.key"
    const val KEY_OLLAMA = "ollama.url"
    const val KEY_COMPATIBLE = "compatible.api.key"
    // const val KEY_COMPATIBLE_URL = "compatible.base.url"

    private const val SUBSYSTEM = "AI Assistant Provocations"

    fun saveKeyAsPassword(credentialKey: String, credential: String?, credentialUrl: String? = null) {
        val attributes = createCredentialAttributes(credentialKey)
        if (credential == null) {
            PasswordSafe.instance[attributes] = null // delete the credential
            return
        }

        val credentials = Credentials(credentialUrl, credential)
        PasswordSafe.instance[attributes] = credentials
    }

    fun getCredentials(credentialKey: String): Credentials? {
        val attributes = createCredentialAttributes(credentialKey)
        return PasswordSafe.instance[attributes]
    }

    fun getCredentialsByProvider(provider: LlmProvider): Credentials? {
        val credentialKey = getCredentialKeyByProvider(provider) ?: return null
        return getCredentials(credentialKey)
    }

    fun deleteCredentials(credentialKey: String) {
        val attributes = createCredentialAttributes(credentialKey)
        PasswordSafe.instance[attributes] = null
    }

    fun getCredentialKeyByProvider(provider: LlmProvider): String? {
        return when (provider) {
            LlmProvider.ANTHROPIC -> KEY_ANTHROPIC
            LlmProvider.OPENAI -> KEY_OPENAI
            LlmProvider.GOOGLE -> KEY_GOOGLE
            LlmProvider.OLLAMA -> KEY_OLLAMA
            LlmProvider.OPENAI_COMPATIBLE -> KEY_COMPATIBLE
        }
    }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        val serviceName = generateServiceName(SUBSYSTEM, key)
        return CredentialAttributes(serviceName)
    }
}
