package com.cleanspace.app.core.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.data.ScanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Runs once a day in the background. If there's a meaningful amount of
 * reclaimable space, it nudges the user with a notification. It never deletes
 * anything on its own — the user always confirms inside the app.
 */
@HiltWorker
class ScanWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repo: ScanRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (!CsPermissions.hasMediaAccess(applicationContext)) return Result.success()

        val data = runCatching { repo.dashboard() }.getOrElse {
            return if (runAttemptCount < 2) Result.retry() else Result.success()
        }

        if (data.reclaimableBytes >= NOTIFY_THRESHOLD) {
            ScanNotifier.showReclaimable(applicationContext, data.reclaimableBytes)
        }
        return Result.success()
    }

    companion object {
        /** Only bug the user when there's at least ~200 MB to free. */
        const val NOTIFY_THRESHOLD = 200L * 1024 * 1024
    }
}
