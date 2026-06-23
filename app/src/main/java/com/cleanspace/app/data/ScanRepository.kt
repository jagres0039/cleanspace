package com.cleanspace.app.data

import android.content.Context
import android.net.Uri
import com.cleanspace.app.core.scan.AppScanner
import com.cleanspace.app.core.scan.DuplicateScanner
import com.cleanspace.app.core.scan.DuplicateSet
import com.cleanspace.app.core.scan.FileDeleter
import com.cleanspace.app.core.scan.HiddenScanner
import com.cleanspace.app.core.scan.MediaScanner
import com.cleanspace.app.core.scan.ScannedFile
import com.cleanspace.app.core.scan.StorageScanner
import com.cleanspace.app.core.scan.TrashManager
import com.cleanspace.app.core.scan.WhatsAppScanner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Aggregates every scanner behind one injectable entry point so ViewModels stay
 * thin. All scan methods are suspend + run on IO inside their scanners.
 */
@Singleton
class ScanRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val media = MediaScanner(context)
    private val duplicateScanner = DuplicateScanner(context)
    private val appScanner = AppScanner(context)
    private val storageScanner = StorageScanner(context)
    private val whatsAppScanner = WhatsAppScanner(context)
    private val hiddenScanner = HiddenScanner(context)

    val trash = TrashManager(context)
    val deleter = FileDeleter(context)

    suspend fun largeFiles(): List<ScannedFile> = media.scanLargeFiles()

    suspend fun allFiles(): List<ScannedFile> = media.scanAllFiles()

    suspend fun duplicateSets(): List<DuplicateSet> =
        duplicateScanner.findDuplicates(media.scanAllFiles())

    fun keeperOf(set: DuplicateSet) = duplicateScanner.keeperOf(set)

    suspend fun installedApps(): List<AppScanner.AppInfo> = appScanner.scanInstalledApps()

    suspend fun storageSummary(): StorageScanner.StorageSummary = storageScanner.summarize()

    suspend fun whatsAppGroups(): List<WhatsAppScanner.WaGroup> = whatsAppScanner.scan()

    suspend fun hiddenItems(): List<HiddenScanner.HiddenItem> = hiddenScanner.scan()

    suspend fun trashedItems(): List<ScannedFile> = trash.listTrashed()

    /**
     * Moves [uris] to the system trash (30-day safety net) when supported,
     * otherwise deletes via ContentResolver. Returns an IntentSender the caller
     * must launch to obtain user confirmation, or null when nothing to confirm.
     */
    suspend fun trashOrDelete(uris: List<Uri>) =
        if (trash.isSupported()) {
            trash.trashRequest(uris)
        } else {
            deleter.delete(uris).consentRequest
        }

    /**
     * Deletes hidden folders/files directly by path (needs All-files access).
     * Returns the number of paths successfully removed.
     */
    suspend fun deletePaths(paths: List<String>): Int = withContext(Dispatchers.IO) {
        var removed = 0
        for (p in paths) {
            val ok = runCatching { File(p).deleteRecursively() }.getOrDefault(false)
            if (ok) removed++
        }
        removed
    }

    /**
     * Size of CleanSpace's OWN cache (internal + external). This is the only
     * cache we are allowed to clear silently — third-party app caches can't be
     * touched without the system Settings UI (Play policy compliant).
     */
    fun ownCacheBytes(): Long =
        dirSize(context.cacheDir) + dirSize(context.externalCacheDir)

    /** Clears CleanSpace's own cache. Returns the number of bytes freed. */
    suspend fun clearOwnCache(): Long = withContext(Dispatchers.IO) {
        val before = ownCacheBytes()
        runCatching { context.cacheDir?.deleteRecursively() }
        runCatching { context.externalCacheDir?.deleteRecursively() }
        (before - ownCacheBytes()).coerceAtLeast(0L)
    }

    private fun dirSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var total = 0L
        val stack = ArrayDeque<File>()
        stack.addLast(dir)
        while (stack.isNotEmpty()) {
            val f = stack.removeLast()
            val kids = f.listFiles() ?: continue
            for (k in kids) if (k.isFile) total += k.length() else stack.addLast(k)
        }
        return total
    }

    /** Snapshot used by the dashboard + background worker (single media scan). */
    data class DashboardData(
        val summary: StorageScanner.StorageSummary,
        val largeBytes: Long,
        val largeCount: Int,
        val duplicateBytes: Long,
        val duplicateCount: Int,
        val whatsAppBytes: Long,
        val junkBytes: Long,
        val junkCount: Int,
        val ownCacheBytes: Long,
    ) {
        /** Junk files + CleanSpace's own cache — the part we can clean directly. */
        val junkAndCacheBytes: Long get() = junkBytes + ownCacheBytes

        /**
         * Conservative “safe to reclaim” estimate: duplicates + WhatsApp media +
         * junk (temp/thumbnails/leftovers) + the app's own cache. Excludes other
         * apps' caches, which Android won't let us delete silently.
         */
        val reclaimableBytes: Long
            get() = duplicateBytes + whatsAppBytes + junkBytes + ownCacheBytes
    }

    suspend fun dashboard(): DashboardData = withContext(Dispatchers.IO) {
        val files = media.scanAllFiles()
        val summary = storageScanner.summarize(files)
        val large = files.filter { it.sizeBytes >= LARGE_FILE_THRESHOLD }
        val dups = duplicateScanner.findDuplicates(files)
        val wa = whatsAppScanner.scan(files)
        // Only the safe (pre-selectable) junk counts toward the reclaimable total.
        val junk = hiddenScanner.scan().filter { it.safe }
        DashboardData(
            summary = summary,
            largeBytes = large.sumOf { it.sizeBytes },
            largeCount = large.size,
            duplicateBytes = dups.sumOf { it.reclaimableBytes },
            duplicateCount = dups.sumOf { it.files.size },
            whatsAppBytes = wa.sumOf { it.totalBytes },
            junkBytes = junk.sumOf { it.sizeBytes },
            junkCount = junk.sumOf { it.itemCount },
            ownCacheBytes = ownCacheBytes(),
        )
    }

    companion object {
        const val LARGE_FILE_THRESHOLD = 100L * 1024 * 1024 // 100 MB
    }
}
