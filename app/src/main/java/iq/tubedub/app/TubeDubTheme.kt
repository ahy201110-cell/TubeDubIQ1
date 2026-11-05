package iq.tubedub.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection

private val TubeDubDarkScheme = darkColorScheme(
    primary = Color(0xFF9B82FF),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF53DDE5),
    onSecondary = Color(0xFF07191B),
    background = Color(0xFF0A0A14),
    onBackground = Color(0xFFF5F3FF),
    surface = Color(0xFF151526),
    onSurface = Color(0xFFF5F3FF),
    surfaceVariant = Color(0xFF25243A),
    onSurfaceVariant = Color(0xFFAAA8C1),
    outline = Color(0xFF34324D),
    error = Color(0xFFFF879D)
)

@Composable
fun TubeDubTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = TubeDubDarkScheme,
            typography = Typography(),
            content = content
        )
    }
}
