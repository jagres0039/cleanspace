package com.cleanspace.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * CleanSpace color tokens.
 *
 * Reskinned for the Soft UI / Neumorphism look: surfaces share a single soft
 * background color and rely on dual shadows (light top-left, dark bottom-right)
 * instead of borders. The legacy flat tokens are kept for reference but the
 * extended color set now points at the neumorphism palette.
 */
object CsPalette {
    // Brand gradient stops (logo / primary accents)
    val BrandGreenLight = Color(0xFF5BE291)
    val BrandGreen = Color(0xFF28B870)
    val BrandGreenDark = Color(0xFF178A51)

    // ---- Legacy flat (kept for reference) ----
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

    // ---- Neumorphism / Soft UI ----
    // Light: soft cool-gray base, white highlight, muted gray shadow.
    val NeuLightBg = Color(0xFFE6E9F0)
    val NeuLightShadowDark = Color(0xFFC3C8D6)
    val NeuLightShadowLight = Color(0xFFFFFFFF)
    val NeuLightText = Color(0xFF3A4056)
    val NeuLightTextSoft = Color(0xFF7E8499)
    val NeuLightTextFaint = Color(0xFFA7ADC0)

    // Dark: deep slate base, lighter highlight, near-black shadow.
    val NeuDarkBg = Color(0xFF292C34)
    val NeuDarkShadowDark = Color(0xFF1E2027)
    val NeuDarkShadowLight = Color(0xFF343843)
    val NeuDarkText = Color(0xFFE6E9F2)
    val NeuDarkTextSoft = Color(0xFF969CAD)
    val NeuDarkTextFaint = Color(0xFF646A7B)

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
 *
 * The neumorphism fields ([neuBg], [neuShadowDark], [neuShadowLight]) drive the
 * soft-shadow helpers in Neumorphism.kt.
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
    // neumorphism surface + shadows
    val neuBg: Color,
    val neuShadowDark: Color,
    val neuShadowLight: Color,
    val isDark: Boolean,
)

val LightExtendedColors = CsExtendedColors(
    bgSoft = CsPalette.NeuLightBg,
    surface = CsPalette.NeuLightBg,
    surfaceHover = CsPalette.NeuLightBg,
    textSoft = CsPalette.NeuLightTextSoft,
    textFaint = CsPalette.NeuLightTextFaint,
    border = Color(0x00000000),
    borderSoft = Color(0x00000000),
    info = CsPalette.LightBlue,
    warn = CsPalette.LightOrange,
    danger = CsPalette.Chart8,
    success = CsPalette.Chart3,
    photo = CsPalette.Chart6,
    video = CsPalette.Chart5,
    apps = CsPalette.Chart1,
    whatsapp = CsPalette.Chart3,
    duplicate = CsPalette.Chart8,
    neuBg = CsPalette.NeuLightBg,
    neuShadowDark = CsPalette.NeuLightShadowDark,
    neuShadowLight = CsPalette.NeuLightShadowLight,
    isDark = false,
)

val DarkExtendedColors = CsExtendedColors(
    bgSoft = CsPalette.NeuDarkBg,
    surface = CsPalette.NeuDarkBg,
    surfaceHover = CsPalette.NeuDarkBg,
    textSoft = CsPalette.NeuDarkTextSoft,
    textFaint = CsPalette.NeuDarkTextFaint,
    border = Color(0x00000000),
    borderSoft = Color(0x00000000),
    info = CsPalette.DarkBlue,
    warn = CsPalette.DarkOrange,
    danger = CsPalette.Chart8,
    success = CsPalette.Chart3,
    photo = CsPalette.Chart6,
    video = CsPalette.Chart5,
    apps = CsPalette.Chart1,
    whatsapp = CsPalette.Chart3,
    duplicate = CsPalette.Chart8,
    neuBg = CsPalette.NeuDarkBg,
    neuShadowDark = CsPalette.NeuDarkShadowDark,
    neuShadowLight = CsPalette.NeuDarkShadowLight,
    isDark = true,
)
