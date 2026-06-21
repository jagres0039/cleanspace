package com.cleanspace.app.core.scan

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Finds WhatsApp media by matching its well-known storage paths. Works off the
 * MediaStore scan (so no extra permission beyond media/all-files access).
 *
 * Modern WhatsApp stores under:
 *   Android/media/com.whatsapp/WhatsApp/Media/...
 * Legacy installs used the top-level /WhatsApp/Media/... folder.
 */
class WhatsAppScanner(private val context: Context) {

    enum class WaBucket { IMAGES, VIDEO, VOICE, AUDIO, DOCUMENTS, STATUS, STICKERS, GIF, OTHER }

    data class WaGroup(
        val bucket: WaBucket,
        val files: List<ScannedFile>,
    ) {
        val totalBytes: Long get() = files.sumOf { it.sizeBytes }
    }

    suspend fun scan(allFiles: List<ScannedFile>? = null): List<WaGroup> =
        withContext(Dispatchers.IO) {
            val files = allFiles ?: MediaScanner(context).scanAllFiles()
            val waFiles = files.filter { isWhatsAppPath(it.path) }
            waFiles
                .groupBy { bucketOf(it.path) }
                .map { (bucket, list) -> WaGroup(bucket, list.sortedByDescending { it.sizeBytes }) }
                .sortedByDescending { it.totalBytes }
        }

    private fun isWhatsAppPath(path: String?): Boolean {
        val p = path?.lowercase() ?: return false
        return "com.whatsapp" in p || "/whatsapp/" in p
    }

    private fun bucketOf(path: String?): WaBucket {
        val p = path?.lowercase() ?: return WaBucket.OTHER
        return when {
            ".statuses" in p || "/statuses" in p -> WaBucket.STATUS
            "whatsapp stickers" in p || "/stickers" in p -> WaBucket.STICKERS
            "whatsapp animated gifs" in p || "/gif" in p -> WaBucket.GIF
            "whatsapp voice notes" in p || "/voice" in p || "/ptt" in p -> WaBucket.VOICE
            "whatsapp video" in p || "/video" in p -> WaBucket.VIDEO
            "whatsapp audio" in p || "/audio" in p -> WaBucket.AUDIO
            "whatsapp documents" in p || "/documents" in p -> WaBucket.DOCUMENTS
            "whatsapp images" in p || "/images" in p -> WaBucket.IMAGES
            else -> WaBucket.OTHER
        }
    }
}
