package com.cleanspace.app.ui.screens.scanning

import android.content.Context
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.data.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI phases for the one-tap Deep Clean flow. */
sealed interface DeepCleanUi {
    data object NeedsPermission : DeepCleanUi
    data class Scanning(val state: ScanningState) : DeepCleanUi
    data class Done(val freedBytes: Long, val fileCount: Int) : DeepCleanUi
    data class Error(val message: String) : DeepCleanUi
}

/**
 * Drives Deep Clean: scans in real time, then moves only *safe* junk to the
 * system Trash (30-day recoverable):
 *  - redundant duplicate copies (keeps the largest in each set)
 *  - safe hidden junk (thumbnails / temp / empty / leftover) when All-files access is granted
 * Media deletion needs the user's system confirmation (IntentSender) on Android 11+.
 */
@HiltViewModel
class DeepCleanViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<DeepCleanUi>(DeepCleanUi.Scanning(initialScan()))
    val state = _state.asStateFlow()

    private val _confirm = MutableSharedFlow<IntentSender>(extraBufferCapacity = 1)
    val confirm = _confirm.asSharedFlow()

    private var freedBytes = 0L
    private var freedCount = 0
    private var pendingDupBytes = 0L
    private var pendingDupCount = 0

    init { start() }

    fun start() {
        viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = DeepCleanUi.NeedsPermission
                return@launch
            }
            freedBytes = 0L; freedCount = 0; pendingDupBytes = 0L; pendingDupCount = 0
            runCatching { runDeepClean() }
                .onFailure { _state.value = DeepCleanUi.Error(it.message ?: "Deep Clean gagal") }
        }
    }

    private suspend fun runDeepClean() {
        emit(0.08f, 0, "Memulai Deep Clean…")
        delay(250)

        // Step 0 — scan media
        val files = repo.allFiles()
        emit(0.35f, 1, "${files.size} file diperiksa")
        delay(250)

        // Step 1 — duplicates (keep the largest copy in each set, trash the rest)
        val redundant = repo.duplicateSets().flatMap { set ->
            set.files.sortedByDescending { it.sizeBytes }.drop(1)
        }
        pendingDupBytes = redundant.sumOf { it.sizeBytes }
        pendingDupCount = redundant.size
        emit(0.6f, 2, "${redundant.size} salinan duplikat ditemukan")
        delay(250)

        // Step 2 — safe hidden junk (only with All-files access)
        val safeHidden = if (CsPermissions.hasAllFilesAccess()) {
            runCatching { repo.hiddenItems() }.getOrDefault(emptyList()).filter { it.safe }
        } else emptyList()
        emit(0.82f, 3, "${safeHidden.size} folder sampah ditemukan")
        delay(250)

        // Step 3 — clean. Hidden junk deletes directly (path-based, no system dialog).
        if (safeHidden.isNotEmpty()) {
            repo.deletePaths(safeHidden.map { it.path })
            freedBytes += safeHidden.sumOf { it.sizeBytes }
            freedCount += safeHidden.sumOf { it.itemCount }
        }
        emit(0.95f, 3, "Mengosongkan ruang…")
        delay(200)

        // Duplicate media needs user confirmation via IntentSender on Android 11+.
        if (redundant.isNotEmpty()) {
            val sender = repo.trashOrDelete(redundant.map { it.uri })
            if (sender != null) {
                _confirm.emit(sender)
                return // wait for onConfirmed()
            }
            freedBytes += pendingDupBytes
            freedCount += pendingDupCount
        }
        finish()
    }

    /** Called after the system delete/trash dialog returns. */
    fun onConfirmed(approved: Boolean) {
        if (approved) {
            freedBytes += pendingDupBytes
            freedCount += pendingDupCount
        }
        finish()
    }

    private fun finish() {
        _state.value = DeepCleanUi.Done(freedBytes, freedCount)
    }

    private fun emit(progress: Float, activeIndex: Int, path: String) {
        _state.value = DeepCleanUi.Scanning(buildScan(progress, activeIndex, path))
    }

    private fun buildScan(progress: Float, activeIndex: Int, path: String) = ScanningState(
        progress = progress,
        currentPath = path,
        steps = STEP_LABELS.mapIndexed { i, label ->
            ScanStep(
                label = label,
                status = when {
                    i < activeIndex -> ScanStepStatus.Done
                    i == activeIndex -> ScanStepStatus.Active
                    else -> ScanStepStatus.Pending
                },
            )
        },
    )

    private fun initialScan() = buildScan(0.04f, 0, "Menyiapkan Deep Clean…")

    companion object {
        private val STEP_LABELS = listOf(
            "Memindai foto & video",
            "Mendeteksi file duplikat",
            "Memeriksa folder tersembunyi",
            "Membersihkan & mengosongkan",
        )
    }
}
