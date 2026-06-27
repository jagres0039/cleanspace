package com.cleanspace.app.ui.theme

import android.graphics.Paint as NativePaint
import android.graphics.Path as NativePath
import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Neumorphism (Soft UI) shadow helpers.
 *
 * Both surfaces share a single background color and get their depth purely from
 * two soft shadows: a light highlight on the top-left and a darker shadow on the
 * bottom-right. [neuRaised] makes an element appear extruded; [neuInset] makes it
 * appear pressed/sunken.
 *
 * Implementation note: shadows are rendered with the native canvas shadow layer,
 * which is hardware-accelerated from API 28+. On API 26-27 the element simply
 * renders flat (no crash).
 */

/** Extruded/raised soft surface (cards, buttons, badges, nav chips). */
fun Modifier.neuRaised(
    backgroundColor: Color,
    shadowDark: Color,
    shadowLight: Color,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
): Modifier = this.drawBehind {
    val r = cornerRadius.toPx()
    val off = elevation.toPx()
    val blur = off * 2f
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val rect = RectF(0f, 0f, size.width, size.height)
        val paint = NativePaint(NativePaint.ANTI_ALIAS_FLAG).apply {
            color = backgroundColor.toArgb()
        }
        // Dark shadow, bottom-right.
        paint.setShadowLayer(blur, off, off, shadowDark.toArgb())
        native.drawRoundRect(rect, r, r, paint)
        // Light highlight, top-left.
        paint.setShadowLayer(blur, -off, -off, shadowLight.toArgb())
        native.drawRoundRect(rect, r, r, paint)
    }
}

/** Pressed/sunken soft surface (inset wells: tracks, unchecked boxes, badges). */
fun Modifier.neuInset(
    backgroundColor: Color,
    shadowDark: Color,
    shadowLight: Color,
    cornerRadius: Dp = 16.dp,
    depth: Dp = 4.dp,
): Modifier = this.drawBehind {
    val r = cornerRadius.toPx()
    val off = depth.toPx()
    val blur = off * 2f
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val rect = RectF(0f, 0f, size.width, size.height)
        // Base fill.
        val fill = NativePaint(NativePaint.ANTI_ALIAS_FLAG).apply {
            color = backgroundColor.toArgb()
        }
        native.drawRoundRect(rect, r, r, fill)
        // Clip to the rounded rect so the inner shadows stay inside.
        val save = native.save()
        val clip = NativePath().apply { addRoundRect(rect, r, r, NativePath.Direction.CW) }
        native.clipPath(clip)
        // A frame path (big rect with the rounded rect cut out). Casting a shadow
        // from this frame projects the shadow inward -> inset look.
        val cut = NativePath().apply {
            fillType = NativePath.FillType.EVEN_ODD
            addRect(-size.width, -size.height, size.width * 2f, size.height * 2f, NativePath.Direction.CW)
            addRoundRect(rect, r, r, NativePath.Direction.CW)
        }
        val sh = NativePaint(NativePaint.ANTI_ALIAS_FLAG).apply {
            color = backgroundColor.toArgb()
        }
        // Dark inner shadow from the top-left edge.
        sh.setShadowLayer(blur, off, off, shadowDark.toArgb())
        native.drawPath(cut, sh)
        // Light inner highlight from the bottom-right edge.
        sh.setShadowLayer(blur, -off, -off, shadowLight.toArgb())
        native.drawPath(cut, sh)
        native.restoreToCount(save)
    }
}
