package net.joshuabrandes.llm

import net.joshuabrandes.model.AssistanceMode
import net.joshuabrandes.model.ToolSession

object Prompt {
    fun codeGeneration(session: ToolSession) = buildString {
        appendLine("The developer wants to achieve: ${session.intent}")
        appendLine("Language: ${session.codeContext.language}")
        appendLine("Surrounding code:\n${session.codeContext.surroundingCode}")
        when (session.mode) {
            AssistanceMode.AUTOCOMPLETE ->
                appendLine("Generate a complete implementation. Code only, no explanation.")
            AssistanceMode.GUIDED ->
                appendLine("""
                    Generate a partial implementation that demonstrates the key approach,
                    but leave meaningful gaps for the developer to fill. 
                    Add a short comment explaining your structural decision.
                """.trimIndent())
        }
    }

    fun provocation(session: ToolSession) = buildString {
        appendLine("The developer wanted to: ${session.intent}")
        appendLine("Language: ${session.codeContext.language}")
        appendLine("Surrounding code:\n${session.codeContext.surroundingCode}")
        appendLine("The generated code was:\n${session.generatedCode}")
        appendLine("""
            Analyze if the code achieves the stated intent.
            Return exactly 2-3 short, pointed questions or observations that:
            - Challenge assumptions or identify edge cases
            - Suggest a meaningfully different approach (not just style)
            - Point out what's missing relative to the stated intent
            Note: Provocations are not necessarily meant to induce change. The goal is to challenge beliefs and make the developer think about the code they just generated/wrote.
            Be direct and specific. No filler. Return as a JSON array of strings.
        """.trimIndent())
    }

}
