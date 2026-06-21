package com.cleanspace.app.core.scan

import android.net.Uri

/** Broad media bucket used for grouping & icons across scan results. */
enum class MediaCategory { IMAGE, VIDEO, AUDIO, DOCUMENT, ARCHIVE, APK, OTHER }

/**
 * A single file discovered by a scanner. [uri] is a content:// URI usable for
 * preview and deletion via the ContentResolver; [path] is best-effort and may
 * be null on newer Android (scoped storage).
 */
data class ScannedFile(
    val id: Long,
    val uri: Uri,
    val name: String,
    val path: String?,
    val sizeBytes: Long,
    val mimeType: String?,
    val dateModifiedMillis: Long,
    val category: MediaCategory,
) {
    val extension: String
        get() = name.substringAfterLast('.', "").lowercase()
}

/** A set of byte-identical files (same size + content hash). */
data class DuplicateSet(
    val hash: String,
    val files: List<ScannedFile>,
) {
    /** Bytes reclaimable if we keep one copy and delete the rest. */
    val reclaimableBytes: Long
        get() = if (files.size <= 1) 0L else files.drop(1).sumOf { it.sizeBytes }
}

/** Maps a mime type / extension to a coarse [MediaCategory]. */
fun categoryFor(mimeType: String?, name: String): MediaCategory {
    val mime = mimeType?.lowercase().orEmpty()
    val ext = name.substringAfterLast('.', "").lowercase()
    return when {
        mime.startsWith("image/") -> MediaCategory.IMAGE
        mime.startsWith("video/") -> MediaCategory.VIDEO
        mime.startsWith("audio/") -> MediaCategory.AUDIO
        mime == "application/vnd.android.package-archive" || ext == "apk" -> MediaCategory.APK
        ext in setOf("zip", "rar", "7z", "tar", "gz") -> MediaCategory.ARCHIVE
        ext in setOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt") -> MediaCategory.DOCUMENT
        else -> MediaCategory.OTHER
    }
}
