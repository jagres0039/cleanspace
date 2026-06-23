package com.cleanspace.app.core.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/** Central place to (re)schedule CleanSpace's background work. */
object CleanWork {
    private const val SCAN_WORK = "cleanspace-scan"

    fun schedulePeriodicScan(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<ScanWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SCAN_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    /** Cancels the daily background scan (used when the user disables it in Settings). */
    fun cancelPeriodicScan(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SCAN_WORK)
    }
}
