package com.cleanspace.app.core.scan

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Deletes media via the ContentResolver.
 *
 * Android storage rules mean an app can't always silently delete files it
 * doesn't own. This helper:
 *  - On API 30+ returns a single [IntentSender] (MediaStore.createDeleteRequest)
 *    so the user confirms the batch in ONE system dialog.
 *  - On older versions, deletes directly and surfaces a [RecoverableSecurityException]
 *    IntentSender when the system requires per-item consent.
 *
 * Callers launch the returned IntentSender with an ActivityResult launcher and,
 * on success, treat the requested URIs as deleted.
 */
class FileDeleter(private val context: Context) {

    data class DeleteResult(
        /** URIs deleted immediately without further consent. */
        val deleted: List<Uri>,
        /** If non-null, launch this to obtain user consent for the rest. */
        val consentRequest: IntentSender?,
        /** URIs awaiting the consent dialog above. */
        val pending: List<Uri>,
    )

    suspend fun delete(uris: List<Uri>): DeleteResult = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) return@withContext DeleteResult(emptyList(), null, emptyList())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // One batched system confirmation for everything.
            val pi = MediaStore.createDeleteRequest(context.contentResolver, uris)
            return@withContext DeleteResult(
                deleted = emptyList(),
                consentRequest = pi.intentSender,
                pending = uris,
            )
        }

        val deleted = ArrayList<Uri>()
        var consent: IntentSender? = null
        val pending = ArrayList<Uri>()
        for (uri in uris) {
            try {
                val rows = context.contentResolver.delete(uri, null, null)
                if (rows > 0) deleted += uri
            } catch (e: RecoverableSecurityException) {
                if (consent == null) {
                    consent = e.userAction.actionIntent.intentSender
                }
                pending += uri
            } catch (_: SecurityException) {
                pending += uri
            }
        }
        DeleteResult(deleted = deleted, consentRequest = consent, pending = pending)
    }

    companion object {
        /** Convenience for the common ScannedFile case. */
        fun urisOf(files: List<ScannedFile>): List<Uri> = files.map { it.uri }

        /** Whether deletion needs an Activity context (API 30+ always does). */
        fun requiresActivity(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

        @Suppress("UNUSED_PARAMETER")
        fun ensureActivity(context: Context): Activity? = context as? Activity
    }
}
