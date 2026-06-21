package com.cleanspace.app.core.scan

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CleanSpace safety net. Instead of deleting outright, media is moved to the
 * system trash where Android keeps it ~30 days and lets the user restore it.
 *
 * On API 30+ this uses [MediaStore.createTrashRequest] (one batched system
 * dialog). Below 30 there is no system trash, so callers should fall back to
 * [FileDeleter]. Restore uses the same request with trashed=false.
 */
class TrashManager(private val context: Context) {

    /** Whether the OS provides a restorable trash for media. */
    fun isSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    /** Returns an IntentSender to confirm moving [uris] to trash, or null if unsupported. */
    fun trashRequest(uris: List<Uri>): IntentSender? {
        if (uris.isEmpty() || !isSupported()) return null
        return MediaStore.createTrashRequest(context.contentResolver, uris, true).intentSender
    }

    /** Returns an IntentSender to restore [uris] from trash, or null if unsupported. */
    fun restoreRequest(uris: List<Uri>): IntentSender? {
        if (uris.isEmpty() || !isSupported()) return null
        return MediaStore.createTrashRequest(context.contentResolver, uris, false).intentSender
    }

    /** Lists items currently in the trash (newest first). Empty below API 30. */
    suspend fun listTrashed(): List<ScannedFile> = withContext(Dispatchers.IO) {
        if (!isSupported()) return@withContext emptyList()
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.DATA,
        )
        val queryArgs = Bundle().apply {
            putInt(MediaStore.QUERY_ARG_MATCH_TRASHED, MediaStore.MATCH_ONLY)
            putString(
                android.content.ContentResolver.QUERY_ARG_SORT_COLUMNS,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
            )
            putInt(
                android.content.ContentResolver.QUERY_ARG_SORT_DIRECTION,
                android.content.ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
            )
        }
        val out = ArrayList<ScannedFile>()
        context.contentResolver.query(collection, projection, queryArgs, null)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val mimeCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val dataCol = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val name = c.getString(nameCol) ?: "file_$id"
                val mime = if (c.isNull(mimeCol)) null else c.getString(mimeCol)
                val path = if (c.isNull(dataCol)) null else c.getString(dataCol)
                out += ScannedFile(
                    id = id,
                    uri = android.content.ContentUris.withAppendedId(collection, id),
                    name = name,
                    path = path,
                    sizeBytes = c.getLong(sizeCol),
                    mimeType = mime,
                    dateModifiedMillis = c.getLong(dateCol) * 1000L,
                    category = categoryFor(mime, name),
                )
            }
        }
        out
    }
}
