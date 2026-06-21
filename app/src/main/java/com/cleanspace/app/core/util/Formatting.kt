package com.cleanspace.app.core.util

import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.math.pow

/** Human-readable byte size, e.g. 1536 -> "1.5 KB", 0 -> "0 B". Base-1024. */
fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt().coerceIn(0, units.size - 1)
    val value = bytes / 1024.0.pow(digitGroups.toDouble())
    return if (digitGroups == 0) {
        "${bytes} B"
    } else {
        val rounded = (value * 10).toLong() / 10.0
        "${rounded} ${units[digitGroups]}"
    }
}

/** Compact relative "last used / modified" label from an epoch-millis timestamp. */
fun formatRelativeTime(epochMillis: Long, now: Long = System.currentTimeMillis()): String {
    if (epochMillis <= 0L) return "—"
    val diff = (now - epochMillis).coerceAtLeast(0L)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        days <= 0L -> "hari ini"
        days == 1L -> "kemarin"
        days < 7L -> "$days hari lalu"
        days < 30L -> "${days / 7} minggu lalu"
        days < 365L -> "${days / 30} bulan lalu"
        else -> "${days / 365} tahun lalu"
    }
}

/** Threshold (bytes) for what CleanSpace considers a "large" file: 100 MB. */
const val LARGE_FILE_THRESHOLD_BYTES: Long = 100L * 1024L * 1024L
