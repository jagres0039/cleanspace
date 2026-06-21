package com.cleanspace.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * CleanSpace color tokens.
 *
 * Mirrors the design spec (§3). Material's [androidx.compose.material3.ColorScheme]
 * only carries the "semantic" subset; everything else (chart palette, soft borders,
 * faint text, tonal backgrounds) lives in [CsExtendedColors] and is provided via
 * a CompositionLocal in Theme.kt.
 */
object CsPalette {
    // Brand gradient stops (logo / primary accents)
    val BrandGreenLight = Color(0xFF5BE291)
    val BrandGreen = Color(0xFF28B870)
    val BrandGreenDark = Color(0xFF178A51)

    // ---- Light ----
    val LightBg = Color(0xFFFFFFFF)
    val LightBgSoft = Color(0xFFF9F9F8)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceHover = Color(0xFFF6F5F4)
    val LightText = Color(0xFF191918)
    val LightTextSoft = Color(0x8A000000) // 54%
    val LightTextFaint = Color(0x4D000000) // 30%
    val LightBorder = Color(0x1A000000) // 10%
    val LightBorderSoft = Color(0x0D000000) // 5%
    val LightBlue = Color(0xFF2783DE)
    val LightOrange = Color(0xFFD5803B)
    val LightRed = Color(0xFFE56458)

    // ---- Dark ----
    val DarkBg = Color(0xFF000000)
    val DarkBgSoft = Color(0xFF191918)
    val DarkSurface = Color(0xFF191918)
    val DarkSurfaceHover = Color(0xFF31302E)
    val DarkText = Color(0xFFFFFFFF)
    val DarkTextSoft = Color(0x80FFFFFF) // 50%
    val DarkTextFaint = Color(0x4DFFFFFF) // 30%
    val DarkBorder = Color(0x33FFFFFF) // 20%
    val DarkBorderSoft = Color(0x1AFFFFFF) // 10%
    val DarkBlue = Color(0xFF5E9FE8)
    val DarkOrange = Color(0xFFDE9255)
    val DarkRed = Color(0xFFE97366)

    // ---- Chart / category (shared light & dark) ----
    val Chart1 = Color(0xFF5E9FE8) // Apps (blue)
    val Chart2 = Color(0xFFEAC26B) // yellow
    val Chart3 = Color(0xFF72BC8F) // brand green / success
    val Chart4 = Color(0xFFBF8EDA) // purple
    val Chart5 = Color(0xFFDE9255) // Video (orange)
    val Chart6 = Color(0xFFDF84A8) // Photo (pink)
    val Chart7 = Color(0xFF4FB9C9) // teal
    val Chart8 = Color(0xFFE97366) // Duplicate / danger (red)
}

/**
 * Extended, non-Material colors used across CleanSpace surfaces.
 * Access via `MaterialTheme` extension `colorsExt` (see Theme.kt).
 */
@Immutable
data class CsExtendedColors(
    val bgSoft: Color,
    val surface: Color,
    val surfaceHover: Color,
    val textSoft: Color,
    val textFaint: Color,
    val border: Color,
    val borderSoft: Color,
    val info: Color,
    val warn: Color,
    val danger: Color,
    val success: Color,
    // category palette
    val photo: Color,
    val video: Color,
    val apps: Color,
    val whatsapp: Color,
    val duplicate: Color,
    val isDark: Boolean,
)

val LightExtendedColors = CsExtendedColors(
    bgSoft = CsPalette.LightBgSoft,
    surface = CsPalette.LightSurface,
    surfaceHover = CsPalette.LightSurfaceHover,
    textSoft = CsPalette.LightTextSoft,
    textFaint = CsPalette.LightTextFaint,
    border = CsPalette.LightBorder,
    borderSoft = CsPalette.LightBorderSoft,
    info = CsPalette.LightBlue,
    warn = CsPalette.LightOrange,
    danger = CsPalette.Chart8,
    success = CsPalette.Chart3,
    photo = CsPalette.Chart6,
    video = CsPalette.Chart5,
    apps = CsPalette.Chart1,
    whatsapp = CsPalette.Chart3,
    duplicate = CsPalette.Chart8,
    isDark = false,
)

val DarkExtendedColors = CsExtendedColors(
    bgSoft = CsPalette.DarkBgSoft,
    surface = CsPalette.DarkSurface,
    surfaceHover = CsPalette.DarkSurfaceHover,
    textSoft = CsPalette.DarkTextSoft,
    textFaint = CsPalette.DarkTextFaint,
    border = CsPalette.DarkBorder,
    borderSoft = CsPalette.DarkBorderSoft,
    info = CsPalette.DarkBlue,
    warn = CsPalette.DarkOrange,
    danger = CsPalette.Chart8,
    success = CsPalette.Chart3,
    photo = CsPalette.Chart6,
    video = CsPalette.Chart5,
    apps = CsPalette.Chart1,
    whatsapp = CsPalette.Chart3,
    duplicate = CsPalette.Chart8,
    isDark = true,
)
