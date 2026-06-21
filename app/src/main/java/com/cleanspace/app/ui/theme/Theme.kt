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

private val LightColorScheme = lightColorScheme(
    primary = CsPalette.Chart3,
    onPrimary = Color.White,
    secondary = CsPalette.LightBlue,
    error = CsPalette.Chart8,
    background = CsPalette.LightBg,
    onBackground = CsPalette.LightText,
    surface = CsPalette.LightSurface,
    onSurface = CsPalette.LightText,
    surfaceVariant = CsPalette.LightBgSoft,
    outline = CsPalette.LightBorder,
)

private val DarkColorScheme = darkColorScheme(
    primary = CsPalette.Chart3,
    onPrimary = Color.White,
    secondary = CsPalette.DarkBlue,
    error = CsPalette.Chart8,
    background = CsPalette.DarkBg,
    onBackground = CsPalette.DarkText,
    surface = CsPalette.DarkSurface,
    onSurface = CsPalette.DarkText,
    surfaceVariant = CsPalette.DarkBgSoft,
    outline = CsPalette.DarkBorder,
)

val LocalCsExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Root theme. Wraps the app in the CleanSpace Material 3 scheme plus the
 * extended (non-Material) color set. Follows the system dark/light setting.
 * Dynamic color is intentionally disabled to keep the green brand consistent.
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
