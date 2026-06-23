package com.cleanspace.app.ui.screens.largest

import android.content.Context
import android.content.IntentSender
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.scan.MediaCategory
import com.cleanspace.app.core.scan.ScannedFile
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.common.categoryLabel
import com.cleanspace.app.ui.common.toLargeFile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LargestFilesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Optional category filter passed via the "category" nav arg (MediaCategory.name).
    // When blank/unknown we fall back to the classic \u2265 100 MB large-files view.
    private val categoryFilter: MediaCategory? =
        savedStateHandle.get<String>("category")
            ?.takeIf { it.isNotBlank() }
            ?.let { runCatching { MediaCategory.valueOf(it) }.getOrNull() }

    /** Top-bar title: the category label when filtered, otherwise "File besar". */
    val title: String = categoryFilter?.let { categoryLabel(it) } ?: "File besar"

    /** True when this screen is a category browse (any size), not the 100 MB view. */
    val isCategoryView: Boolean = categoryFilter != null

    private val _state = MutableStateFlow<ScanUiState<List<LargeFile>>>(ScanUiState.Loading)
    val state = _state.asStateFlow()

    /** One-shot delete-confirmation IntentSenders to launch from the UI. */
    private val _confirm = MutableSharedFlow<IntentSender>(extraBufferCapacity = 1)
    val confirm = _confirm.asSharedFlow()

    private var scanned: List<ScannedFile> = emptyList()

    init { load() }

    fun load() {
        viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            _state.value = ScanUiState.Loading
            runCatching {
                val cat = categoryFilter
                if (cat == null) {
                    // Default view: every file \u2265 100 MB across all categories.
                    repo.largeFiles()
                } else {
                    // Category browse: all files of this category, largest first.
                    repo.allFiles()
                        .filter { it.category == cat }
                        .sortedByDescending { it.sizeBytes }
                }
            }
                .onSuccess { files ->
                    scanned = files
                    _state.value = ScanUiState.Ready(files.map { it.toLargeFile() })
                }
                .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal memindai") }
        }
    }

    fun delete(ids: List<String>) {
        viewModelScope.launch {
            val idSet = ids.toSet()
            val targets = scanned.filter { it.id.toString() in idSet }
            if (targets.isEmpty()) return@launch
            // Media \u2192 system trash; non-media (zip/apk/docs) \u2192 deleted by path.
            // Guarded so a thrown delete-request can't crash the app.
            runCatching { repo.deleteScanned(targets) }
                .onSuccess { outcome ->
                    if (outcome.confirmRequest != null) _confirm.emit(outcome.confirmRequest) else load()
                }
                .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal menghapus") }
        }
    }

    /** Called after the system confirmation dialog completes successfully. */
    fun onConfirmed() = load()
}
