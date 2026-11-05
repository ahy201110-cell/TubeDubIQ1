package iq.tubedub.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.File
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private val bg = Color.rgb(7, 17, 12)
    private val panel = Color.rgb(15, 31, 22)
    private val green = Color.rgb(72, 213, 151)
    private val white = Color.rgb(238, 247, 241)
    private val muted = Color.rgb(165, 186, 174)
    private lateinit var urlField: EditText
    private lateinit var sourceField: EditText
    private lateinit var translatedField: EditText
    private lateinit var geminiKeyField: EditText
    private lateinit var fishKeyField: EditText
    private lateinit var voiceField: EditText
    private lateinit var status: TextView
    private lateinit var securePrefs: SecurePrefs
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var player: MediaPlayer? = null
    private val importCode = 1001
    private val exportCode = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securePrefs = SecurePrefs(this)
        tts = TextToSpeech(this, this)
        buildScreen()
        loadSettings()
    }

    private fun buildScreen() {
        window.statusBarColor = bg
        window.navigationBarColor = bg
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(28))
            layoutDirection = LinearLayout.LAYOUT_DIRECTION_RTL
        }
        scroll.addView(root)

        root.addView(text("TubeDub IQ", 29f, green, true))
        root.addView(text("ترجمة الفيديو ودبلجته باللهجة العراقية", 15f, white, true))
        root.addView(text("نسخة أولية مستقلة — استخراج ترجمة، تحويل للعراقي، وصوت", 12f, muted, false).apply {
            setPadding(0, dp(5), 0, dp(16))
        })

        section(root, "1 · رابط الفيديو")
        urlField = edit("الصق رابط YouTube هنا", 1)
        root.addView(urlField)
        root.addView(button("استخراج ترجمة YouTube") { extractYoutube() })

        section(root, "2 · ملف الترجمة")
        root.addView(button("استيراد ملف SRT / VTT / TXT") { pickSubtitle() })
        sourceField = edit("النص الأصلي أو الترجمة المستخرجة تظهر هنا", 8)
        root.addView(sourceField)

        section(root, "3 · الترجمة العراقية")
        root.addView(button("حوّل النص إلى لهجة عراقية") { translate() })
        translatedField = edit("الترجمة العراقية تظهر هنا وتكدر تعدّلها", 8)
        root.addView(translatedField)
        root.addView(button("تصدير الترجمة إلى ملف SRT") { exportSubtitle() })

        section(root, "4 · الدبلجة الصوتية")
        root.addView(button("دبلجة عبر Fish Audio") { generateFishAudio() })
        root.addView(button("نطق محلي من الجهاز") { speakLocal() })

        section(root, "5 · إعدادات المفاتيح")
        geminiKeyField = edit("مفتاح Google AI Studio", 1, true)
        fishKeyField = edit("مفتاح Fish Audio", 1, true)
        voiceField = edit("Fish Audio Voice ID (اختياري)", 1)
        root.addView(geminiKeyField)
        root.addView(fishKeyField)
        root.addView(voiceField)
        root.addView(button("حفظ الإعدادات بأمان") { saveSettings() })

        status = text("جاهز. ابدأ بلصق رابط أو استيراد ملف ترجمة.", 13f, muted, false).apply {
            setPadding(dp(12), dp(14), dp(12), dp(14))
            setBackgroundColor(panel)
        }
        root.addView(status, marginParams(top = 14))
        setContentView(scroll)
    }

    private fun section(parent: LinearLayout, title: String) {
        parent.addView(text(title, 17f, green, true).apply {
            setPadding(0, dp(18), 0, dp(8))
        })
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.RIGHT
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

    private fun edit(hintText: String, lines: Int, password: Boolean = false): EditText = EditText(this).apply {
        hint = hintText
        setTextColor(white)
        setHintTextColor(muted)
        textSize = 14f
        gravity = Gravity.TOP or Gravity.RIGHT
        textDirection = android.view.View.TEXT_DIRECTION_RTL
        setPadding(dp(12), dp(10), dp(12), dp(10))
        setBackgroundColor(panel)
        minLines = lines
        maxLines = if (lines == 1) 1 else 12
        if (password) inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        else if (lines == 1) inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_URI
        layoutParams = marginParams(bottom = 8)
    }

    private fun button(label: String, action: () -> Unit): Button = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 14f
        setTextColor(Color.rgb(5, 25, 15))
        setBackgroundTintList(android.content.res.ColorStateList.valueOf(green))
        setOnClickListener { action() }
        layoutParams = marginParams(bottom = 7)
    }

    private fun marginParams(top: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(top)
            bottomMargin = dp(bottom)
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun extractYoutube() {
        val link = urlField.text.toString().trim()
        if (link.isBlank()) return showStatus("الصق رابط الفيديو أولاً")
        work("دا أستخرج الترجمة من YouTube...") {
            val result = YouTubeCaptionClient.extract(link)
            runOnUiThread {
                sourceField.setText(result.srt)
                showStatus("تم استخراج الترجمة. اللغة: ${result.language}" + if (result.autoGenerated) " — تلقائية" else " — يدوية")
            }
        }
    }

    private fun pickSubtitle() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/x-subrip", "text/vtt", "text/plain", "application/octet-stream"))
        }
        startActivityForResult(intent, importCode)
    }

    private fun exportSubtitle() {
        if (translatedField.text.isNullOrBlank()) return showStatus("ماكو ترجمة عراقية حتى نصدرها")
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/x-subrip"
            putExtra(Intent.EXTRA_TITLE, "tubedub-iraqi.srt")
        }
        startActivityForResult(intent, exportCode)
    }

    @Deprecated("Using document picker result for compatibility with the minimal Termux build")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        when (requestCode) {
            importCode -> readSubtitle(uri)
            exportCode -> writeSubtitle(uri)
        }
    }

    private fun readSubtitle(uri: Uri) {
        work("دا أقرأ ملف الترجمة...") {
            val raw = contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                ?: throw IllegalStateException("ما گدرت أفتح الملف")
            val normalized = SubtitleParser.normalize(raw)
            runOnUiThread {
                sourceField.setText(normalized)
                showStatus("تم استيراد الملف. راجع النص قبل الترجمة.")
            }
        }
    }

    private fun writeSubtitle(uri: Uri) {
        work("دا أحفظ ملف الترجمة...") {
            val output = SubtitleParser.outputForExport(translatedField.text.toString())
            contentResolver.openOutputStream(uri)?.bufferedWriter(Charsets.UTF_8)?.use { it.write(output) }
                ?: throw IllegalStateException("ما گدرت أكتب الملف")
            runOnUiThread { showStatus("تم تصدير الملف.") }
        }
    }

    private fun translate() {
        val source = sourceField.text.toString().trim()
        val key = geminiKeyField.text.toString().trim()
        if (source.isBlank()) return showStatus("استخرج الترجمة أو استورد ملف أولاً")
        if (key.isBlank()) return showStatus("أدخل مفتاح Google AI Studio واحفظه بالإعدادات")
        work("Gemini دا يحوّل النص للعراقي. النص الطويل ينقسم إلى أجزاء...") {
            val translated = GeminiTranslator.translateToIraqi(source, key)
            runOnUiThread {
                translatedField.setText(translated)
                showStatus("اكتملت الترجمة العراقية. راجع الأسماء والتوقيتات قبل التصدير.")
            }
        }
    }

    private fun generateFishAudio() {
        val text = translatedField.text.toString().trim()
        val key = fishKeyField.text.toString().trim()
        val voice = voiceField.text.toString().trim()
        if (text.isBlank()) return showStatus("اكتب أو ترجم النص العراقي أولاً")
        if (key.isBlank()) return showStatus("أدخل مفتاح Fish Audio واحفظ الإعدادات")
        if (text.length > 3500) return showStatus("النص أطول من حد النسخة الأولية للدبلجة. قسّمه إلى مقاطع أقصر حالياً.")
        work("Fish Audio دا يولّد الصوت...") {
            val file = File(cacheDir, "tubedub-voice-${System.currentTimeMillis()}.mp3")
            FishAudioClient.synthesize(text, key, voice, file)
            runOnUiThread {
                showStatus("اكتمل توليد الصوت. دا أشغّله...")
                playAudio(file)
            }
        }
    }

    private fun playAudio(file: File) {
        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener { it.start(); showStatus("الصوت دا يشتغل") }
                setOnCompletionListener { it.release(); if (player === it) player = null; showStatus("انتهى تشغيل الصوت") }
                setOnErrorListener { mp, _, _ -> mp.release(); player = null; showStatus("صار خطأ بتشغيل ملف الصوت"); true }
                prepareAsync()
            }
        } catch (e: Exception) {
            showStatus("ما گدرت أشغّل الصوت: ${e.message}")
        }
    }

    private fun speakLocal() {
        val text = translatedField.text.toString().trim()
        if (text.isBlank()) return showStatus("اكتب النص العراقي أولاً")
        if (!ttsReady) return showStatus("محرك النطق بالجهاز بعده ما جاهز")
        tts?.language = Locale("ar", "IQ")
        tts?.speak(text.take(3500), TextToSpeech.QUEUE_FLUSH, null, "tubedub-local-tts")
        showStatus("طلبت النطق المحلي. جودة اللهجة تعتمد على الأصوات المثبتة بجهازك.")
    }

    private fun saveSettings() {
        try {
            securePrefs.put("gemini", geminiKeyField.text.toString().trim())
            securePrefs.put("fish", fishKeyField.text.toString().trim())
            securePrefs.put("voice", voiceField.text.toString().trim())
            showStatus("تم حفظ المفاتيح مشفّرة بمخزن Android Keystore")
        } catch (e: Exception) {
            showStatus("تعذر حفظ الإعدادات: ${e.message}")
        }
    }

    private fun loadSettings() {
        geminiKeyField.setText(securePrefs.get("gemini"))
        fishKeyField.setText(securePrefs.get("fish"))
        voiceField.setText(securePrefs.get("voice"))
    }

    private fun work(message: String, operation: () -> Unit) {
        showStatus(message)
        thread(name = "tubedub-worker") {
            try {
                operation()
            } catch (e: Exception) {
                runOnUiThread { showStatus(e.message ?: "صار خطأ غير معروف") }
            }
        }
    }

    private fun showStatus(message: String) {
        if (::status.isInitialized) status.text = message
    }

    override fun onInit(statusCode: Int) {
        ttsReady = statusCode == TextToSpeech.SUCCESS
        if (ttsReady) tts?.language = Locale("ar", "IQ")
    }

    override fun onDestroy() {
        player?.release()
        player = null
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
