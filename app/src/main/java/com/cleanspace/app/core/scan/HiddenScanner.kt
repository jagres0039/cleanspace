package com.cleanspace.app.core.scan

import android.content.Context
import android.os.Environment
import com.cleanspace.app.core.permissions.CsPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Scans the external storage tree for hidden/junk folders that are safe (or
 * mostly safe) to remove. Requires All-files access; without it, returns empty.
 *
 * Categories mirror the UI's HiddenKind:
 *  - THUMBNAILS: regenerable .thumbnails caches.
 *  - EMPTY:      empty directories left behind by apps.
 *  - TEMP:       .tmp/.temp/cache leftovers.
 *  - LEFTOVER:   Android/data & Android/obb dirs of uninstalled apps.
 */
class HiddenScanner(private val context: Context) {

    enum class HiddenKind { THUMBNAILS, EMPTY, TEMP, LEFTOVER }

    data class HiddenItem(
        val path: String,
        val kind: HiddenKind,
        val sizeBytes: Long,
        val itemCount: Int,
        /** True when removal is low-risk and can be pre-selected. */
        val safe: Boolean,
    )

    suspend fun scan(maxDepth: Int = 6): List<HiddenItem> = withContext(Dispatchers.IO) {
        if (!CsPermissions.hasAllFilesAccess()) return@withContext emptyList()
        val root = Environment.getExternalStorageDirectory() ?: return@withContext emptyList()
        val out = ArrayList<HiddenItem>()
        walk(root, 0, maxDepth, out)
        out.sortedByDescending { it.sizeBytes }
    }

    private fun walk(dir: File, depth: Int, maxDepth: Int, out: MutableList<HiddenItem>) {
        if (depth > maxDepth || !dir.isDirectory) return
        val children = dir.listFiles() ?: return
        val name = dir.name.lowercase()

        when {
            name == ".thumbnails" -> {
                out += HiddenItem(dir.path, HiddenKind.THUMBNAILS, dirSize(dir), countFiles(dir), safe = true)
                return // don't descend; whole folder is regenerable
            }
            isLeftover(dir) -> {
                out += HiddenItem(dir.path, HiddenKind.LEFTOVER, dirSize(dir), countFiles(dir), safe = false)
                return
            }
            children.isEmpty() -> {
                out += HiddenItem(dir.path, HiddenKind.EMPTY, 0L, 0, safe = true)
                return
            }
        }

        // Collect temp files in this dir.
        var tempBytes = 0L
        var tempCount = 0
        for (child in children) {
            if (child.isFile && isTempFile(child.name)) {
                tempBytes += child.length()
                tempCount++
            }
        }
        if (tempCount > 0) {
            out += HiddenItem(dir.path, HiddenKind.TEMP, tempBytes, tempCount, safe = true)
        }

        for (child in children) {
            if (child.isDirectory) walk(child, depth + 1, maxDepth, out)
        }
    }

    private fun isTempFile(n: String): Boolean {
        val lower = n.lowercase()
        return lower.endsWith(".tmp") || lower.endsWith(".temp") || lower.endsWith(".log") ||
            lower.endsWith(".crdownload") || lower.endsWith(".part")
    }

    private fun isLeftover(dir: File): Boolean {
        val p = dir.path.lowercase()
        return p.contains("/android/data/") && dir.name.contains('.') && isProbablyOrphan(dir)
    }

    private fun isProbablyOrphan(dir: File): Boolean {
        return try {
            val pkg = dir.name
            context.packageManager.getPackageInfo(pkg, 0)
            false // package installed -> not orphan
        } catch (_: Exception) {
            true // not installed -> leftover
        }
    }

    private fun dirSize(dir: File): Long {
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

    private fun countFiles(dir: File): Int {
        var count = 0
        val stack = ArrayDeque<File>()
        stack.addLast(dir)
        while (stack.isNotEmpty()) {
            val f = stack.removeLast()
            val kids = f.listFiles() ?: continue
            for (k in kids) if (k.isFile) count++ else stack.addLast(k)
        }
        return count
    }
}
