package com.cleanspace.app.core.scan

import android.content.ContentResolver
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.MessageDigest

/**
 * Finds byte-identical duplicate files.
 *
 * Strategy (cheap -> expensive, to avoid hashing everything):
 *  1. Bucket candidates by exact byte size (instant, from MediaStore).
 *  2. Within each size bucket of 2+ files, hash a SAMPLE (head+tail) to split
 *     obvious non-matches.
 *  3. Within each sample-hash group of 2+ files, compute a FULL hash to confirm
 *     true duplicates.
 *
 * Files that fail to open (deleted/permission) are skipped gracefully.
 */
class DuplicateScanner(private val context: Context) {

    private val resolver: ContentResolver get() = context.contentResolver

    suspend fun findDuplicates(
        files: List<ScannedFile>,
        minSizeBytes: Long = 1L,
    ): List<DuplicateSet> = withContext(Dispatchers.IO) {
        val bySize = files
            .filter { it.sizeBytes >= minSizeBytes }
            .groupBy { it.sizeBytes }
            .filterValues { it.size > 1 }

        val confirmed = ArrayList<DuplicateSet>()

        for ((_, sameSize) in bySize) {
            // Stage 2: split by sample hash.
            val bySample = sameSize.groupBy { runCatching { sampleHash(it) }.getOrNull() }
                .filterKeys { it != null }
                .filterValues { it.size > 1 }

            for ((_, sampleGroup) in bySample) {
                // Stage 3: confirm by full hash.
                val byFull = sampleGroup.groupBy { runCatching { fullHash(it) }.getOrNull() }
                    .filterKeys { it != null }
                    .filterValues { it.size > 1 }

                for ((hash, dupes) in byFull) {
                    confirmed += DuplicateSet(
                        hash = hash!!,
                        files = dupes.sortedByDescending { it.dateModifiedMillis },
                    )
                }
            }
        }
        confirmed.sortedByDescending { it.reclaimableBytes }
    }

    /**
     * Chooses which file to KEEP in a set: prefer the one in a "primary" folder
     * (e.g. Camera/DCIM), then the oldest (original), as the keeper.
     */
    fun keeperOf(set: DuplicateSet): ScannedFile {
        return set.files.minWithOrNull(
            compareByDescending<ScannedFile> { isPrimaryLocation(it.path) }
                .thenBy { it.dateModifiedMillis },
        ) ?: set.files.first()
    }

    private fun isPrimaryLocation(path: String?): Boolean {
        val p = path?.lowercase() ?: return false
        return "/dcim/" in p || "/camera/" in p || "/pictures/" in p
    }

    private fun sampleHash(file: ScannedFile): String {
        val sampleSize = 64 * 1024 // 64 KB head sample
        val md = MessageDigest.getInstance("MD5")
        md.update(longToBytes(file.sizeBytes))
        resolver.openInputStream(file.uri)?.use { input ->
            val buf = ByteArray(sampleSize)
            val read = input.readFully(buf)
            if (read > 0) md.update(buf, 0, read)
        } ?: error("cannot open ${file.uri}")
        return md.digest().toHex()
    }

    private fun fullHash(file: ScannedFile): String {
        val md = MessageDigest.getInstance("MD5")
        resolver.openInputStream(file.uri)?.use { input ->
            val buf = ByteArray(128 * 1024)
            while (true) {
                val read = input.read(buf)
                if (read <= 0) break
                md.update(buf, 0, read)
            }
        } ?: error("cannot open ${file.uri}")
        return md.digest().toHex()
    }

    private fun InputStream.readFully(buf: ByteArray): Int {
        var total = 0
        while (total < buf.size) {
            val read = read(buf, total, buf.size - total)
            if (read <= 0) break
            total += read
        }
        return total
    }

    private fun longToBytes(v: Long): ByteArray {
        val b = ByteArray(8)
        for (i in 0 until 8) b[i] = (v shr (i * 8)).toByte()
        return b
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
