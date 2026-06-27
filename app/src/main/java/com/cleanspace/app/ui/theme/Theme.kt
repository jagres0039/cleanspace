package com.cleanspace.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Soft UI / Neumorphism scheme. Surfaces share the soft background color; depth
 * comes from the neumorphism shadow helpers rather than contrasting surfaces or
 * borders. Dynamic color stays disabled to keep the green brand consistent.
 */
private val LightColorScheme = lightColorScheme(
    primary = CsPalette.BrandGreen,
    onPrimary = Color.White,
    secondary = CsPalette.LightBlue,
    error = CsPalette.Chart8,
    background = CsPalette.NeuLightBg,
    onBackground = CsPalette.NeuLightText,
    surface = CsPalette.NeuLightBg,
    onSurface = CsPalette.NeuLightText,
    surfaceVariant = CsPalette.NeuLightBg,
    outline = Color(0x00000000),
)

private val DarkColorScheme = darkColorScheme(
    primary = CsPalette.Chart3,
    onPrimary = Color.White,
    secondary = CsPalette.DarkBlue,
    error = CsPalette.Chart8,
    background = CsPalette.NeuDarkBg,
    onBackground = CsPalette.NeuDarkText,
    surface = CsPalette.NeuDarkBg,
    onSurface = CsPalette.NeuDarkText,
    surfaceVariant = CsPalette.NeuDarkBg,
    outline = Color(0x00000000),
)

val LocalCsExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Root theme. Wraps the app in the CleanSpace Material 3 scheme plus the
 * extended (non-Material) color set. Follows the system dark/light setting.
 */
@Composable
fun CleanSpaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extended = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalCsExtendedColors provides extended) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CsTypography,
            content = content,
        )
    }
}

/** Convenient accessor: `MaterialTheme.colorsExt.success` etc. */
val MaterialTheme.colorsExt: CsExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCsExtendedColors.current
