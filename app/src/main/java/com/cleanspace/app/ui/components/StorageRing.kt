package com.cleanspace.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.CsText
import com.cleanspace.app.ui.theme.colorsExt

/**
 * Circular storage gauge (design spec §7, Dashboard).
 *
 * @param fraction 0f..1f portion of storage used — drives the gradient sweep.
 * @param usedLabel big center label, e.g. "82.4 GB".
 * @param totalLabel secondary label, e.g. "of 128 GB".
 */
@Composable
fun StorageRing(
    fraction: Float,
    usedLabel: String,
    totalLabel: String,
    modifier: Modifier = Modifier,
) {
    val ext = MaterialTheme.colorsExt
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(900),
        label = "ringSweep",
    )
    val sweepBrush = Brush.sweepGradient(
        0.0f to CsPalette.BrandGreenLight,
        0.5f to CsPalette.BrandGreen,
        1.0f to CsPalette.BrandGreenDark,
    )
    Box(modifier = modifier.size(172.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(172.dp)) {
            val stroke = 18.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)
            // track
            drawArc(
                color = ext.surfaceHover,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            // progress
            drawArc(
                brush = sweepBrush,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(usedLabel, style = CsText.displayBig, color = MaterialTheme.colorScheme.onSurface)
            Text(totalLabel, style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
        }
    }
}
