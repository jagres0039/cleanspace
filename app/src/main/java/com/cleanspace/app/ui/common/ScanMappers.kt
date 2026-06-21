package com.cleanspace.app.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.cleanspace.app.core.scan.AppScanner
import com.cleanspace.app.core.scan.DuplicateSet
import com.cleanspace.app.core.scan.MediaCategory
import com.cleanspace.app.core.scan.ScannedFile
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.core.util.formatRelativeTime
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.screens.apps.AppEntry
import com.cleanspace.app.ui.screens.duplicates.DuplicateGroup
import com.cleanspace.app.ui.screens.duplicates.DuplicateItem
import com.cleanspace.app.ui.screens.largest.LargeFile
import com.cleanspace.app.ui.theme.CsPalette
import java.util.concurrent.TimeUnit

private val chartPalette = listOf(
    CsPalette.Chart1, CsPalette.Chart2, CsPalette.Chart3, CsPalette.Chart4,
    CsPalette.Chart5, CsPalette.Chart6, CsPalette.Chart7, CsPalette.Chart8,
)

fun iconFor(category: MediaCategory): ImageVector = when (category) {
    MediaCategory.IMAGE -> CsIcons.Image
    MediaCategory.VIDEO -> CsIcons.Film
    MediaCategory.AUDIO -> CsIcons.Mic
    MediaCategory.DOCUMENT -> CsIcons.FileText
    MediaCategory.ARCHIVE -> CsIcons.Box
    MediaCategory.APK -> CsIcons.Smartphone
    MediaCategory.OTHER -> CsIcons.Box
}

fun tintFor(category: MediaCategory): Color = when (category) {
    MediaCategory.IMAGE -> CsPalette.Chart6
    MediaCategory.VIDEO -> CsPalette.Chart5
    MediaCategory.AUDIO -> CsPalette.Chart3
    MediaCategory.DOCUMENT -> CsPalette.Chart2
    MediaCategory.ARCHIVE -> CsPalette.Chart1
    MediaCategory.APK -> CsPalette.Chart4
    MediaCategory.OTHER -> CsPalette.Chart8
}

fun categoryLabel(category: MediaCategory): String = when (category) {
    MediaCategory.IMAGE -> "Foto"
    MediaCategory.VIDEO -> "Video"
    MediaCategory.AUDIO -> "Audio"
    MediaCategory.DOCUMENT -> "Dokumen"
    MediaCategory.ARCHIVE -> "Arsip"
    MediaCategory.APK -> "APK"
    MediaCategory.OTHER -> "Lainnya"
}

fun ScannedFile.toLargeFile(): LargeFile = LargeFile(
    id = id.toString(),
    icon = iconFor(category),
    tint = tintFor(category),
    name = name,
    meta = "${categoryLabel(category)} · ${formatRelativeTime(dateModifiedMillis)}",
    sizeLabel = formatBytes(sizeBytes),
)

fun DuplicateSet.toGroup(keeperId: Long): DuplicateGroup {
    val keeper = files.firstOrNull { it.id == keeperId } ?: files.first()
    return DuplicateGroup(
        id = hash,
        title = "${keeper.name} (${files.size} salinan)",
        sizeEachLabel = formatBytes(keeper.sizeBytes),
        items = files.map { f ->
            DuplicateItem(
                id = f.id.toString(),
                name = f.name,
                meta = "${formatBytes(f.sizeBytes)} · ${formatRelativeTime(f.dateModifiedMillis)}",
                isBest = f.id == keeper.id,
            )
        },
    )
}

private const val UNUSED_THRESHOLD_DAYS = 30L

fun AppScanner.AppInfo.toEntry(): AppEntry {
    val unused = lastUsedMillis in 1 until
        (System.currentTimeMillis() - TimeUnit.DAYS.toMillis(UNUSED_THRESHOLD_DAYS))
    val tint = chartPalette[(packageName.hashCode() and 0x7fffffff) % chartPalette.size]
    return AppEntry(
        packageName = packageName,
        name = label,
        cacheLabel = if (cacheBytes < 0) "—" else formatBytes(cacheBytes),
        totalSizeLabel = if (totalBytes < 0) "—" else formatBytes(totalBytes),
        lastUsedLabel = if (lastUsedMillis <= 0L) "belum tercatat" else formatRelativeTime(lastUsedMillis),
        unused = unused,
        tint = tint,
    )
}
