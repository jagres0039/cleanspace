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
}
