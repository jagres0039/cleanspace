package com.cleanspace.app.core.scan

import android.app.usage.StorageStatsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.storage.StorageManager
import com.cleanspace.app.core.permissions.CsPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Enumerates user-installed apps with their cache & total size and last-used
 * time. Cache/size and last-used require Usage Access; without it we still list
 * apps but sizes/last-used are reported as unknown (-1).
 */
class AppScanner(private val context: Context) {

    data class AppInfo(
        val packageName: String,
        val label: String,
        val cacheBytes: Long,
        val appBytes: Long,
        val dataBytes: Long,
        /** Epoch millis of last foreground use, or 0 if unknown. */
        val lastUsedMillis: Long,
        val isSystem: Boolean,
    ) {
        val totalBytes: Long get() = appBytes + dataBytes + cacheBytes
    }

    suspend fun scanInstalledApps(includeSystem: Boolean = false): List<AppInfo> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val hasUsage = CsPermissions.hasUsageAccess(context)
            val lastUsedMap = if (hasUsage) lastUsedByPackage() else emptyMap()
            val ssm = context.getSystemService(Context.STORAGE_STATS_SERVICE) as? StorageStatsManager

            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            apps.mapNotNull { info ->
                val isSystem = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                if (isSystem && !includeSystem) return@mapNotNull null
                if (info.packageName == context.packageName) return@mapNotNull null

                val (cache, appSize, data) = queryStats(ssm, info, hasUsage)
                AppInfo(
                    packageName = info.packageName,
                    label = pm.getApplicationLabel(info).toString(),
                    cacheBytes = cache,
                    appBytes = appSize,
                    dataBytes = data,
                    lastUsedMillis = lastUsedMap[info.packageName] ?: 0L,
                    isSystem = isSystem,
                )
            }.sortedByDescending { it.cacheBytes }
        }

    private fun queryStats(
        ssm: StorageStatsManager?,
        info: ApplicationInfo,
        hasUsage: Boolean,
    ): Triple<Long, Long, Long> {
        if (ssm == null || !hasUsage) return Triple(-1L, -1L, -1L)
        return try {
            val uuid = StorageManager.UUID_DEFAULT
            val stats = ssm.queryStatsForPackage(uuid, info.packageName, Process.myUserHandle())
            Triple(stats.cacheBytes, stats.appBytes, stats.dataBytes)
        } catch (_: Exception) {
            Triple(-1L, -1L, -1L)
        }
    }

    /** Maps package -> last foreground-use millis over the last ~180 days. */
    private fun lastUsedByPackage(): Map<String, Long> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return emptyMap()
        val end = System.currentTimeMillis()
        val start = end - TimeUnit.DAYS.toMillis(180)
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end) ?: return emptyMap()
        val map = HashMap<String, Long>()
        for (s in stats) {
            val prev = map[s.packageName] ?: 0L
            if (s.lastTimeUsed > prev) map[s.packageName] = s.lastTimeUsed
        }
        return map
    }

    @Suppress("unused")
    private fun apiGuard() = Build.VERSION.SDK_INT
}
