package iq.tubedub.app

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object GeminiTranslator {
    fun translateToIraqi(source: String, apiKey: String, model: String = "gemini-3.8-flash"): String {
        require(apiKey.isNotBlank()) { "أدخل مفتاح Google AI Studio أولاً" }
        require(source.isNotBlank()) { "ماكو نص حتى نترجمه" }
        val chunks = splitKeepingParagraphs(source, 4200)
        val output = StringBuilder()
        chunks.forEachIndexed { index, chunk ->
            val prompt = """حوّل النص التالي إلى اللهجة العراقية الطبيعية الواضحة، المناسبة للترجمة والدبلجة.
حافظ على أسماء الشخصيات والأماكن والأرقام وترتيب الجمل.
إذا النص يحتوي أرقام أسطر وتوقيت SRT أو VTT، حافظ على كل الأرقام والتوقيتات كما هي حرفياً، ولا تحذف أي سطر.
لا تختصر ولا تضيف شرحاً أو مقدمة أو ملاحظات. أرجع النص المحوّل فقط.
هذا الجزء ${index + 1} من ${chunks.size}:

$chunk"""
            output.append(callInteractions(prompt, apiKey, model).trim())
            if (index < chunks.lastIndex) output.append("\n\n")
        }
        return output.toString().trim()
    }

    private fun callInteractions(prompt: String, apiKey: String, model: String): String {
        val connection = (URL("https://generativelanguage.googleapis.com/v1beta/interactions").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 25_000
            readTimeout = 180_000
            doOutput = true
            setRequestProperty("x-goog-api-key", apiKey)
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }
        try {
            val body = JSONObject().put("model", model).put("input", prompt).put("store", false).toString()
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val responseText = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                val detail = try { JSONObject(responseText).toString() } catch (_: Exception) { responseText.take(500) }
                throw IllegalStateException("Gemini HTTP $code: $detail")
            }
            val root = JSONObject(responseText)
            val outputText = root.optString("output_text")
            if (outputText.isNotBlank()) return outputText
            val steps = root.optJSONArray("steps")
            val pieces = mutableListOf<String>()
            if (steps != null) {
                for (i in 0 until steps.length()) {
                    val step = steps.optJSONObject(i) ?: continue
                    if (step.optString("type") != "model_output") continue
                    val content = step.optJSONArray("content") ?: continue
                    for (j in 0 until content.length()) {
                        val part = content.optJSONObject(j) ?: continue
                        if (part.optString("type") == "text") pieces += part.optString("text")
                    }
                }
            }
            return pieces.joinToString("").ifBlank { throw IllegalStateException("Gemini رجّع استجابة بدون نص") }
        } finally {
            connection.disconnect()
        }
    }

    private fun splitKeepingParagraphs(text: String, maxChars: Int): List<String> {
        if (text.length <= maxChars) return listOf(text)
        val paragraphs = text.replace("\r\n", "\n").split(Regex("\\n\\s*\\n"))
        val result = mutableListOf<String>()
        var current = StringBuilder()
        for (paragraph in paragraphs) {
            var remaining = paragraph
            while (remaining.length > maxChars) {
                if (current.isNotEmpty()) {
                    result += current.toString()
                    current = StringBuilder()
                }
                val cut = remaining.lastIndexOf('\n', maxChars).takeIf { it > maxChars / 2 } ?: maxChars
                result += remaining.substring(0, cut)
                remaining = remaining.substring(cut).trimStart('\n')
            }
            if (current.isNotEmpty() && current.length + paragraph.length + 2 > maxChars) {
                result += current.toString()
                current = StringBuilder()
            }
            if (current.isNotEmpty()) current.append("\n\n")
            current.append(remaining)
        }
        if (current.isNotEmpty()) result += current.toString()
        return result
    }
}
