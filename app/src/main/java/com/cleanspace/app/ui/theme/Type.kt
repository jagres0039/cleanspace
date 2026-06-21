package com.cleanspace.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Typography (design spec §4). System font family (Roboto on Android).
 * Storage numbers should use tabular numerals — apply via [CsText.display]
 * which callers pair with FontFeatureSettings where needed.
 */
val CsTypography = Typography(
    // Display — big storage numbers
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.03).em,
    ),
    // App bar title
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.02).em,
    ),
    // Sub-screen title
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    // Item title
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    // Body
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Caption / meta
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    // Micro — bottom nav, badges
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 12.sp,
    ),
)

/** Extra named styles not covered by the Material slots. */
object CsText {
    val displayBig = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 46.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.03).em,
    )

    /** UPPERCASE section header (.sec). Caller should uppercase the string. */
    val sectionLabel = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.04.em,
    )

    val buttonLabel = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Center,
    )
}
