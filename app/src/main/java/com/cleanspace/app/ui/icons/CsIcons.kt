package com.cleanspace.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * CleanSpace icon set — Feather-style line icons ported 1:1 from the HTML
 * prototype (design spec §6). All icons are stroke-only on a 24x24 viewBox and
 * are meant to be rendered with [androidx.compose.material3.Icon], whose `tint`
 * recolors the stroke (replacing the SVG `currentColor` behaviour).
 *
 * NOTE: never substitute emoji for these in product UI.
 */
object CsIcons {
    // ---- geometry helpers (SVG primitives -> path-data strings) ----
    private fun n(v: Double): String {
        val r = if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
        return r
    }

    private fun circle(cx: Double, cy: Double, r: Double): String =
        "M${n(cx - r)} $cy a $r $r 0 1 0 ${n(2 * r)} 0 a $r $r 0 1 0 ${n(-2 * r)} 0"

    private fun rect(x: Double, y: Double, w: Double, h: Double, rx: Double): String {
        val hw = w - 2 * rx
        val vh = h - 2 * rx
        return "M${n(x + rx)} $y h${n(hw)} a $rx $rx 0 0 1 $rx $rx v${n(vh)} " +
            "a $rx $rx 0 0 1 ${n(-rx)} $rx h${n(-hw)} a $rx $rx 0 0 1 ${n(-rx)} ${n(-rx)} " +
            "v${n(-vh)} a $rx $rx 0 0 1 $rx ${n(-rx)} z"
    }

    private fun poly(points: String, closed: Boolean = false): String {
        val nums = points.trim().split(Regex("[ ,]+")).map { it.toDouble() }
        val sb = StringBuilder()
        var i = 0
        while (i < nums.size - 1) {
            sb.append(if (i == 0) "M" else "L").append(n(nums[i])).append(' ').append(n(nums[i + 1])).append(' ')
            i += 2
        }
        if (closed) sb.append('z')
        return sb.toString()
    }

    private fun line(x1: Double, y1: Double, x2: Double, y2: Double): String =
        "M${n(x1)} ${n(y1)} L${n(x2)} ${n(y2)}"

    private fun icon(vararg d: String): ImageVector {
        val b = ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        for (p in d) {
            b.addPath(
                pathData = PathParser().parsePathString(p).toNodes(),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
        return b.build()
    }

    // ---- navigation ----
    val Home: ImageVector = icon(
        "M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z",
        poly("9 22 9 12 15 12 15 22"),
    )
    val Settings: ImageVector = icon(
        circle(12.0, 12.0, 3.0),
        "M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z",
    )
    val ChevronLeft: ImageVector = icon(poly("15 18 9 12 15 6"))
    val ChevronRight: ImageVector = icon(poly("9 18 15 12 9 6"))

    // ---- categories ----
    val BarChart: ImageVector = icon(
        line(12.0, 20.0, 12.0, 10.0),
        line(18.0, 20.0, 18.0, 4.0),
        line(6.0, 20.0, 6.0, 14.0),
    )
    val Image: ImageVector = icon(
        rect(3.0, 3.0, 18.0, 18.0, 2.0),
        circle(8.5, 8.5, 1.5),
        poly("21 15 16 10 5 21"),
    )
    val Film: ImageVector = icon(
        rect(2.0, 2.0, 20.0, 20.0, 2.18),
        line(7.0, 2.0, 7.0, 22.0),
        line(17.0, 2.0, 17.0, 22.0),
        line(2.0, 12.0, 22.0, 12.0),
        line(2.0, 7.0, 7.0, 7.0),
        line(2.0, 17.0, 7.0, 17.0),
        line(17.0, 17.0, 22.0, 17.0),
        line(17.0, 7.0, 22.0, 7.0),
    )
    val Smartphone: ImageVector = icon(
        rect(5.0, 2.0, 14.0, 20.0, 2.0),
        line(12.0, 18.0, 12.01, 18.0),
    )
    val MessageCircle: ImageVector = icon(
        "M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8z",
    )
    val Box: ImageVector = icon(
        "M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z",
        poly("3.27 6.96 12 12.01 20.73 6.96"),
        line(12.0, 22.08, 12.0, 12.0),
    )
    val Copy: ImageVector = icon(
        rect(9.0, 9.0, 13.0, 13.0, 2.0),
        "M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1",
    )
    val FileText: ImageVector = icon(
        "M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z",
        poly("14 2 14 8 20 8"),
    )
    val Mic: ImageVector = icon(
        rect(9.0, 2.0, 6.0, 12.0, 3.0),
        "M5 10v1a7 7 0 0 0 14 0v-1",
        line(12.0, 19.0, 12.0, 22.0),
    )

    // ---- status / actions ----
    val Trash: ImageVector = icon(
        poly("3 6 5 6 21 6"),
        "M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2",
        line(10.0, 11.0, 10.0, 17.0),
        line(14.0, 11.0, 14.0, 17.0),
    )
    val Search: ImageVector = icon(
        circle(11.0, 11.0, 8.0),
        line(21.0, 21.0, 16.65, 16.65),
    )
    val ShieldCheck: ImageVector = icon(
        "M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z",
        poly("9 12 11 14 15 10"),
    )
    val CheckCircle: ImageVector = icon(
        "M22 11.08V12a10 10 0 1 1-5.93-9.14",
        poly("22 4 12 14.01 9 11.01"),
    )
    val Info: ImageVector = icon(
        circle(12.0, 12.0, 10.0),
        line(12.0, 16.0, 12.0, 12.0),
        line(12.0, 8.0, 12.01, 8.0),
    )
    val Zap: ImageVector = icon(poly("13 2 3 14 12 14 11 22 21 10 12 10 13 2", closed = true))
    val Sparkles: ImageVector = icon(
        "M12 3l1.7 4.8L18.5 9.5l-4.8 1.7L12 16l-1.7-4.8L5.5 9.5l4.8-1.7z",
        "M18.5 14.5l.9 2.4 2.4.9-2.4.9-.9 2.4-.9-2.4-2.4-.9 2.4-.9z",
    )
    val Refresh: ImageVector = icon(
        poly("23 4 23 10 17 10"),
        poly("1 20 1 14 7 14"),
        "M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15",
    )
    val Check: ImageVector = icon(poly("20 6 9 17 4 12"))
}
