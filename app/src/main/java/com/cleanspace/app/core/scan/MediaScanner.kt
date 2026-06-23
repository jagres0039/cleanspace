package com.cleanspace.app.core.scan

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.cleanspace.app.core.util.LARGE_FILE_THRESHOLD_BYTES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Reads files from the system MediaStore. Works with either granular media
 * permissions or All-files access. Returns content:// URIs ready for preview
 * and deletion.
 */
class MediaScanner(private val context: Context) {

    private val resolver: ContentResolver get() = context.contentResolver

    /**
     * All files at or above [minSizeBytes] (default 100 MB), newest-largest first.
     * Scans the combined Files collection so videos, archives, APKs, docs, etc.
     * are all included — not just images.
     */
    suspend fun scanLargeFiles(
        minSizeBytes: Long = LARGE_FILE_THRESHOLD_BYTES,
    ): List<ScannedFile> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Files.getContentUri("external")
        }

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.DATA,
        )
        val selection = "${MediaStore.Files.FileColumns.SIZE} >= ?"
        val args = arrayOf(minSizeBytes.toString())
        val sortOrder = "${MediaStore.Files.FileColumns.SIZE} DESC"

        val results = ArrayList<ScannedFile>()
        resolver.query(collection, projection, selection, args, sortOrder)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val mimeCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val dataCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)

            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val size = c.getLong(sizeCol)
                if (size < minSizeBytes) continue
                val name = c.getString(nameCol) ?: c.getString(dataCol)?.substringAfterLast('/') ?: "file_$id"
                val mime = if (c.isNull(mimeCol)) null else c.getString(mimeCol)
                val path = if (c.isNull(dataCol)) null else c.getString(dataCol)
                // DATE_MODIFIED is in seconds.
                val modified = c.getLong(dateCol) * 1000L
                val category = categoryFor(mime, name)
                // IMPORTANT: build the uri from the TYPE-SPECIFIC media collection
                // (images/video/audio). MediaStore.createTrashRequest /
                // createDeleteRequest reject generic "files" uris with
                // "All requested items must be Media items". Non-media keep the
                // files collection uri (deleted via path / resolver fallback).
                val uri = mediaUriFor(category, id)
                results += ScannedFile(
                    id = id,
                    uri = uri,
                    name = name,
                    path = path,
                    sizeBytes = size,
                    mimeType = mime,
                    dateModifiedMillis = modified,
                    category = category,
                )
            }
        }
        results
    }

    /**
     * Returns the content:// uri in the correct MediaStore collection for [id].
     * Images/Video/Audio go to their typed collections so the system trash &
     * delete request APIs accept them; everything else stays in Files.
     */
    private fun mediaUriFor(category: MediaCategory, id: Long): Uri {
        val base = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (category) {
                MediaCategory.IMAGE -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                MediaCategory.VIDEO -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                MediaCategory.AUDIO -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                else -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            }
        } else {
            @Suppress("DEPRECATION")
            when (category) {
                MediaCategory.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                MediaCategory.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                MediaCategory.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> MediaStore.Files.getContentUri("external")
            }
        }
        return ContentUris.withAppendedId(base, id)
    }

    /**
     * All media files (any size) — used as the input set for duplicate detection.
     * Capped by [limit] to keep scans bounded on huge libraries.
     */
    suspend fun scanAllFiles(limit: Int = 20_000): List<ScannedFile> =
        withContext(Dispatchers.IO) {
            scanLargeFiles(minSizeBytes = 1L).take(limit)
        }
}
