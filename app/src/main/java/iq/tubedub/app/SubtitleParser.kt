package iq.tubedub.app

object SubtitleParser {
    private val timing = Regex(
        "(?:(\\d{1,2}):)?(\\d{2}):(\\d{2})[,.](\\d{3})\\s*-->\\s*(?:(\\d{1,2}):)?(\\d{2}):(\\d{2})[,.](\\d{3}).*"
    )

    fun normalize(input: String): String {
        val clean = input.removePrefix("\uFEFF").trim()
        if (clean.isEmpty()) return ""
        val blocks = clean.replace("\r\n", "\n").replace('\r', '\n')
            .split(Regex("\\n\\s*\\n"))
        val cues = mutableListOf<Pair<String, String>>()
        for (block in blocks) {
            val lines = block.lines().map { it.trimEnd() }.filter { it.isNotBlank() }
            if (lines.isEmpty()) continue
            val timeIndex = lines.indexOfFirst { timing.matches(it.trim()) }
            if (timeIndex >= 0) {
                val time = lines[timeIndex].trim().replace('.', ',')
                val text = lines.drop(timeIndex + 1).joinToString("\n").trim()
                if (text.isNotEmpty()) cues += time to text
            }
        }
        if (cues.isEmpty()) return clean
        return cues.mapIndexed { index, cue -> "${index + 1}\n${cue.first}\n${cue.second}" }
            .joinToString("\n\n")
    }

    fun looksLikeTimedSubtitles(text: String): Boolean =
        text.lineSequence().any { timing.matches(it.trim()) || it.contains(" --> ") }

    fun outputForExport(text: String): String {
        val normalized = normalize(text)
        return if (looksLikeTimedSubtitles(normalized)) normalized + "\n" else normalized
    }
}
