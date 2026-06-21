package com.cleanspace.app.core.scan

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Device storage totals plus a media-category size breakdown. */
class StorageScanner(private val context: Context) {

    data class StorageSummary(
        val totalBytes: Long,
        val freeBytes: Long,
        val usedBytes: Long,
        /** Sum of bytes per media category (from MediaStore; not the whole disk). */
        val byCategory: Map<MediaCategory, Long>,
    ) {
        val usedFraction: Float
            get() = if (totalBytes <= 0L) 0f else (usedBytes.toDouble() / totalBytes).toFloat()
    }

    suspend fun summarize(allFiles: List<ScannedFile>? = null): StorageSummary =
        withContext(Dispatchers.IO) {
            val (total, free) = deviceTotals()
            val files = allFiles ?: MediaScanner(context).scanAllFiles()
            val byCategory = files
                .groupBy { it.category }
                .mapValues { (_, list) -> list.sumOf { it.sizeBytes } }
            StorageSummary(
                totalBytes = total,
                freeBytes = free,
                usedBytes = (total - free).coerceAtLeast(0L),
                byCategory = byCategory,
            )
        }

    /** Returns (totalBytes, freeBytes) for the primary (internal) volume. */
    private fun deviceTotals(): Pair<Long, Long> {
        return try {
            val ssm = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val uuid = StorageManager.UUID_DEFAULT
            val total = ssm.getTotalBytes(uuid)
            val free = ssm.getFreeBytes(uuid)
            total to free
        } catch (_: Exception) {
            // Fallback to StatFs on the data directory.
            val stat = android.os.StatFs(context.filesDir.absolutePath)
            val total = stat.blockCountLong * stat.blockSizeLong
            val free = stat.availableBlocksLong * stat.blockSizeLong
            total to free
        }
    }

    @Suppress("unused")
    private fun apiGuard() = Build.VERSION.SDK_INT // min API 26 already satisfies StorageStatsManager
}
