package com.zaviyanllc.calcworker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Brand palette — deep-space darks with sky + emerald accents. */
object Brand {
    val Sky = Color(0xFF38BDF8)
    val SkySoft = Color(0xFF7DD3FC)
    val Emerald = Color(0xFF34D399)
    val Gold = Color(0xFFFBBF24)
    val Coral = Color(0xFFFB7185)
    val Violet = Color(0xFFA78BFA)
}

private val DarkColors = darkColorScheme(
    primary = Brand.Sky,
    onPrimary = Color(0xFF04121C),
    primaryContainer = Color(0xFF0E2A3D),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = Brand.Emerald,
    tertiary = Brand.Violet,
    background = Color(0xFF06080D),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF0D1420),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF141D2C),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainerLow = Color(0xFF0A0F19),
    surfaceContainerHigh = Color(0xFF182234),
    outline = Color(0xFF263449),
    outlineVariant = Color(0xFF1B2637)
)

private val DimColors = darkColorScheme(
    primary = Brand.SkySoft,
    onPrimary = Color(0xFF0A1622),
    primaryContainer = Color(0xFF1B3448),
    onPrimaryContainer = Color(0xFFD6EFFF),
    secondary = Brand.Emerald,
    tertiary = Brand.Violet,
    background = Color(0xFF0E1218),
    onBackground = Color(0xFFF4F6FA),
    surface = Color(0xFF161C25),
    onSurface = Color(0xFFF4F6FA),
    surfaceVariant = Color(0xFF1E2632),
    onSurfaceVariant = Color(0xFFA8B3C2),
    surfaceContainerLow = Color(0xFF11161D),
    surfaceContainerHigh = Color(0xFF222B38),
    outline = Color(0xFF2E3949),
    outlineVariant = Color(0xFF232C3A)
)

private val CwTypography = Typography(
    displaySmall = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.25).sp),
    headlineSmall = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
)

private val CwShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
)

/** "dark" or "dim" — both deep darks, per the product spec. */
@Composable
fun CalcWorkerTheme(theme: String = "dark", content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (theme == "dim") DimColors else DarkColors,
        typography = CwTypography,
        shapes = CwShapes,
        content = content
    )
}

/** Category accent colors, cycled. */
fun categoryColor(category: String): Color {
    val palette = listOf(Brand.Sky, Brand.Emerald, Brand.Violet, Brand.Gold, Brand.Coral, Brand.SkySoft)
    return palette[(category.hashCode() and Int.MAX_VALUE) % palette.size]
}
