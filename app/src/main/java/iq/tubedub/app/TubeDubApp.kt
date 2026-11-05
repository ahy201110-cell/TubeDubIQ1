package iq.tubedub.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF0A0A14)
private val Panel = Color(0xFF151526)
private val PanelRaised = Color(0xFF1C1B31)
private val Violet = Color(0xFF9B82FF)
private val Cyan = Color(0xFF53DDE5)
private val PrimaryText = Color(0xFFF5F3FF)
private val SecondaryText = Color(0xFFAAA8C1)
private val Stroke = Color(0xFF34324D)

@Composable
fun TubeDubApp(
    videoUrl: String,
    onVideoUrlChange: (String) -> Unit,
    sourceSubtitle: String,
    onSourceSubtitleChange: (String) -> Unit,
    iraqiSubtitle: String,
    onIraqiSubtitleChange: (String) -> Unit,
    geminiKey: String,
    onGeminiKeyChange: (String) -> Unit,
    fishKey: String,
    onFishKeyChange: (String) -> Unit,
    voiceId: String,
    onVoiceIdChange: (String) -> Unit,
    statusMessage: String,
    isWorking: Boolean,
    isPlayingAudio: Boolean,
    onExtractYoutube: () -> Unit,
    onImportSubtitle: () -> Unit,
    onTranslate: () -> Unit,
    onExportSubtitle: () -> Unit,
    onFishDub: () -> Unit,
    onLocalSpeak: () -> Unit,
    onStopAudio: () -> Unit,
    onSaveSettings: () -> Unit
) {
    var selectedPage by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Ink,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF11111F),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedPage == 0,
                    onClick = { selectedPage = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "الرئيسية") },
                    label = { Text("الرئيسية") },
                    colors = navColors()
                )
                NavigationBarItem(
                    selected = selectedPage == 1,
                    onClick = { selectedPage = 1 },
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "الاستوديو") },
                    label = { Text("الاستوديو") },
                    colors = navColors()
                )
                NavigationBarItem(
                    selected = selectedPage == 2,
                    onClick = { selectedPage = 2 },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "الإعدادات") },
                    label = { Text("الإعدادات") },
                    colors = navColors()
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Ink)
        ) {
            AnimatedContent(
                targetState = selectedPage,
                label = "tube-dub-page"
            ) { page ->
                when (page) {
                    1 -> StudioScreen(
                        videoUrl = videoUrl,
                        onVideoUrlChange = onVideoUrlChange,
                        sourceSubtitle = sourceSubtitle,
                        onSourceSubtitleChange = onSourceSubtitleChange,
                        iraqiSubtitle = iraqiSubtitle,
                        onIraqiSubtitleChange = onIraqiSubtitleChange,
                        statusMessage = statusMessage,
                        isWorking = isWorking,
                        isPlayingAudio = isPlayingAudio,
                        onExtractYoutube = onExtractYoutube,
                        onImportSubtitle = onImportSubtitle,
                        onTranslate = onTranslate,
                        onExportSubtitle = onExportSubtitle,
                        onFishDub = onFishDub,
                        onLocalSpeak = onLocalSpeak,
                        onStopAudio = onStopAudio
                    )
                    2 -> SettingsScreen(
                        geminiKey = geminiKey,
                        onGeminiKeyChange = onGeminiKeyChange,
                        fishKey = fishKey,
                        onFishKeyChange = onFishKeyChange,
                        voiceId = voiceId,
                        onVoiceIdChange = onVoiceIdChange,
                        statusMessage = statusMessage,
                        onSaveSettings = onSaveSettings
                    )
                    else -> HomeScreen(
                        statusMessage = statusMessage,
                        isWorking = isWorking,
                        onOpenStudio = { selectedPage = 1 }
                    )
                }
            }
        }
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Cyan,
    selectedTextColor = Cyan,
    indicatorColor = Color(0xFF292643),
    unselectedIconColor = SecondaryText,
    unselectedTextColor = SecondaryText
)

@Composable
private fun HomeScreen(
    statusMessage: String,
    isWorking: Boolean,
    onOpenStudio: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        BrandHeader()
        HeroCard(onOpenStudio = onOpenStudio)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "كل أدوات الدبلجة بمكان واحد",
                color = PrimaryText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "استخرج الترجمة، حوّلها للعراقي، وطلّع الصوت بخطوات واضحة.",
                color = SecondaryText,
                fontSize = 13.sp,
                lineHeight = 21.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureCard(
                number = "01",
                title = "استخراج",
                subtitle = "YouTube وملفات",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                number = "02",
                title = "تعريب عراقي",
                subtitle = "Gemini AI",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                number = "03",
                title = "دبلجة",
                subtitle = "Fish Audio",
                modifier = Modifier.weight(1f)
            )
        }

        SectionHeading("حالة المشروع", "متابعة آخر عملية")
        StatusCard(statusMessage = statusMessage, isWorking = isWorking)

        Text(
            text = "ملاحظة: استخراج ترجمة YouTube يعتمد على توفر مسارات الترجمة للفيديو.",
            color = SecondaryText,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun BrandHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Violet, Color(0xFF5142A7)))),
                contentAlignment = Alignment.Center
            ) {
                Text("TD", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(11.dp))
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text("TubeDub IQ", color = PrimaryText, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("IRAQI VIDEO STUDIO", color = SecondaryText, fontSize = 9.sp, letterSpacing = 1.4.sp)
            }
        }
        Surface(
            color = Color(0xFF1E1C33),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Stroke)
        ) {
            Text(
                text = "BETA  ·  1.1",
                color = Cyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun HeroCard(onOpenStudio: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF34265E), Color(0xFF1A1B38), Color(0xFF12333C))
                )
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Surface(color = Color(0x3353DDE5), shape = RoundedCornerShape(50)) {
                Text(
                    text = "صوت الفيديو… بلهجتنا",
                    color = Cyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
            Text(
                text = "خلّي أي فيديو\nيحچي عراقي",
                color = Color.White,
                fontSize = 29.sp,
                lineHeight = 37.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "ترجمة ذكية ودبلجة صوتية من واجهة وحدة، وبخطوات سهلة.",
                color = Color(0xFFD5D3EA),
                fontSize = 13.sp,
                lineHeight = 21.sp
            )
            Button(
                onClick = onOpenStudio,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cyan,
                    contentColor = Ink
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("افتح استوديو الدبلجة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
            }
        }
    }
}

@Composable
private fun FeatureCard(number: String, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, Stroke)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(number, color = Violet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(title, color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = SecondaryText, fontSize = 9.sp, lineHeight = 13.sp)
        }
    }
}

@Composable
private fun StudioScreen(
    videoUrl: String,
    onVideoUrlChange: (String) -> Unit,
    sourceSubtitle: String,
    onSourceSubtitleChange: (String) -> Unit,
    iraqiSubtitle: String,
    onIraqiSubtitleChange: (String) -> Unit,
    statusMessage: String,
    isWorking: Boolean,
    isPlayingAudio: Boolean,
    onExtractYoutube: () -> Unit,
    onImportSubtitle: () -> Unit,
    onTranslate: () -> Unit,
    onExportSubtitle: () -> Unit,
    onFishDub: () -> Unit,
    onLocalSpeak: () -> Unit,
    onStopAudio: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        ScreenHeading("استوديو الدبلجة", "من رابط الفيديو إلى النص والصوت العراقي")

        SectionCard {
            SectionHeading("01 · رابط الفيديو", "استخراج مسار الترجمة")
            OutlinedTextField(
                value = videoUrl,
                onValueChange = onVideoUrlChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("رابط YouTube") },
                placeholder = { Text("https://youtu.be/...") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                shape = RoundedCornerShape(16.dp)
            )
            PrimaryAction(
                text = "استخراج ترجمة الفيديو",
                enabled = !isWorking && videoUrl.isNotBlank(),
                onClick = onExtractYoutube
            )
        }

        SectionCard {
            SectionHeading("02 · مصدر الترجمة", "استورد ملف أو عدّل النص المستخرج")
            OutlinedButton(
                onClick = onImportSubtitle,
                enabled = !isWorking,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Violet.copy(alpha = 0.6f))
            ) {
                Text("استيراد SRT / VTT / TXT", color = Violet, fontWeight = FontWeight.SemiBold)
            }
            OutlinedTextField(
                value = sourceSubtitle,
                onValueChange = onSourceSubtitleChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 9,
                label = { Text("النص الأصلي") },
                placeholder = { Text("الترجمة المستخرجة تظهر هنا…") },
                shape = RoundedCornerShape(16.dp)
            )
        }

        SectionCard {
            SectionHeading("03 · اللهجة العراقية", "حوّل النص وخلي التوقيتات قابلة للمراجعة")
            PrimaryAction(
                text = "حوّل النص إلى عراقي",
                enabled = !isWorking && sourceSubtitle.isNotBlank(),
                onClick = onTranslate
            )
            OutlinedTextField(
                value = iraqiSubtitle,
                onValueChange = onIraqiSubtitleChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 9,
                label = { Text("الترجمة العراقية") },
                placeholder = { Text("النص العراقي يظهر هنا وتكدر تعدّله…") },
                shape = RoundedCornerShape(16.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                OutlinedButton(
                    onClick = onExportSubtitle,
                    enabled = !isWorking && iraqiSubtitle.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(1.dp, Stroke)
                ) {
                    Text("تصدير SRT", color = PrimaryText, fontSize = 12.sp)
                }
                Button(
                    onClick = onFishDub,
                    enabled = !isWorking && iraqiSubtitle.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Color.White)
                ) {
                    Text("دبلجة Fish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            OutlinedButton(
                onClick = onLocalSpeak,
                enabled = !isWorking && iraqiSubtitle.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text("نطق محلي من الجهاز", color = Cyan)
            }
            if (isPlayingAudio) {
                OutlinedButton(
                    onClick = onStopAudio,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(1.dp, Color(0xFFB56C82))
                ) {
                    Text("إيقاف الصوت", color = Color(0xFFFF9FB4))
                }
            }
        }

        StatusCard(statusMessage = statusMessage, isWorking = isWorking)
        Text(
            text = "حد Fish Audio الحالي 3500 حرف لكل طلب. مزامنة الصوت مع الفيديو غير متوفرة بهالنسخة.",
            color = SecondaryText,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun SettingsScreen(
    geminiKey: String,
    onGeminiKeyChange: (String) -> Unit,
    fishKey: String,
    onFishKeyChange: (String) -> Unit,
    voiceId: String,
    onVoiceIdChange: (String) -> Unit,
    statusMessage: String,
    onSaveSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHeading("الإعدادات", "اربط خدمات الذكاء الاصطناعي واضبط الصوت")
        SectionCard {
            SectionHeading("Google AI Studio", "يُستخدم لترجمة النص إلى اللهجة العراقية")
            SecretField(
                value = geminiKey,
                onValueChange = onGeminiKeyChange,
                label = "مفتاح Gemini API"
            )
        }
        SectionCard {
            SectionHeading("Fish Audio", "يُستخدم لتوليد ملف الدبلجة الصوتي")
            SecretField(
                value = fishKey,
                onValueChange = onFishKeyChange,
                label = "مفتاح Fish Audio API"
            )
            OutlinedTextField(
                value = voiceId,
                onValueChange = onVoiceIdChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Voice ID (اختياري)") },
                placeholder = { Text("اتركه فارغ إذا ما عندك Voice ID") },
                shape = RoundedCornerShape(16.dp)
            )
        }
        PrimaryAction(text = "حفظ الإعدادات بأمان", enabled = true, onClick = onSaveSettings)
        StatusCard(statusMessage = statusMessage, isWorking = false)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF171A2C)),
            border = BorderStroke(1.dp, Stroke)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("خصوصيتك أولاً", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "مفاتيح الخدمات تنحفظ باستخدام SecurePrefs المرتبط بـ Android Keystore. لا تشارك مفاتيحك أو ترفعها إلى GitHub.",
                    color = SecondaryText,
                    fontSize = 12.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun SecretField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    var visible by rememberSaveable(label) { mutableIntStateOf(0) }
    val showText = visible == 1
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (showText) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            TextButton(onClick = { visible = if (showText) 0 else 1 }) {
                Text(if (showText) "إخفاء" else "إظهار", color = Cyan, fontSize = 11.sp)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ScreenHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, color = PrimaryText, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, color = SecondaryText, fontSize = 12.sp, lineHeight = 19.sp)
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = SecondaryText, fontSize = 11.sp, lineHeight = 17.sp)
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, Stroke)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { content() }
    }
}

@Composable
private fun PrimaryAction(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(51.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Violet,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF39354F),
            disabledContentColor = Color(0xFF9290A8)
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun StatusCard(statusMessage: String, isWorking: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = PanelRaised),
        border = BorderStroke(1.dp, Stroke)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("حالة التنفيذ", color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = if (isWorking) Color(0xFF3B315E) else Color(0xFF183B3C),
                    shape = CircleShape
                ) {
                    Text(
                        if (isWorking) "جاري العمل" else "جاهز",
                        color = if (isWorking) Violet else Cyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            Text(statusMessage, color = SecondaryText, fontSize = 12.sp, lineHeight = 19.sp)
            if (isWorking) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = Cyan,
                    trackColor = Color(0xFF34324D)
                )
            }
        }
    }
}
