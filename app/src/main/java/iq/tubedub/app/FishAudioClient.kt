package iq.tubedub.app

import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object FishAudioClient {
    fun synthesize(text: String, apiKey: String, referenceId: String, output: File): File {
        require(apiKey.isNotBlank()) { "أدخل مفتاح Fish Audio أولاً" }
        require(text.isNotBlank()) { "النص العراقي فارغ" }
        val connection = (URL("https://api.fish.audio/v1/tts").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 25_000
            readTimeout = 180_000
            doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("model", "s2.1-pro-free")
        }
        try {
            val body = JSONObject().put("text", text).put("format", "mp3").apply {
                if (referenceId.isNotBlank()) put("reference_id", referenceId)
            }.toString()
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = connection.responseCode
            if (code !in 200..299) {
                val error = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                throw IllegalStateException("Fish Audio HTTP $code: ${error.take(500)}")
            }
            connection.inputStream.use { input -> output.outputStream().use { input.copyTo(it) } }
            if (!output.exists() || output.length() < 100) throw IllegalStateException("ملف الصوت الناتج فارغ أو غير مكتمل")
            return output
        } finally {
            connection.disconnect()
        }
    }
}
