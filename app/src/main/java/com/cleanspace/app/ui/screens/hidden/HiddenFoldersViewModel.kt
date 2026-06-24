package com.cleanspace.app.ui.screens.hidden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.scan.HiddenScanner
import com.cleanspace.app.core.scan.HiddenScanner.HiddenKind as ScanKind
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiddenFoldersViewModel @Inject constructor(
    private val repo: ScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState<List<HiddenFolder>>>(ScanUiState.Loading)
    val state = _state.asStateFlow()

    // True while a delete is running so the UI can show the "Menghapus\u2026" overlay.
    private val _deleting = MutableStateFlow(false)
    val deleting = _deleting.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            if (!CsPermissions.hasAllFilesAccess()) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            _state.value = ScanUiState.Loading
            runCatching { repo.hiddenItems() }
                .onSuccess { items -> _state.value = ScanUiState.Ready(items.map { it.toFolder() }) }
                .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal memindai folder") }
        }
    }

    /** Hidden folder ids ARE their paths, so delete directly. */
    fun delete(ids: List<String>) {
        viewModelScope.launch {
            if (ids.isEmpty()) return@launch
            _deleting.value = true
            try {
                runCatching { repo.deletePaths(ids) }
                load()
            } finally {
                _deleting.value = false
            }
        }
    }

    private fun HiddenScanner.HiddenItem.toFolder(): HiddenFolder = HiddenFolder(
        id = path,
        kind = when (kind) {
            ScanKind.THUMBNAILS -> HiddenKind.Thumbnails
            ScanKind.LEFTOVER -> HiddenKind.Leftover
            ScanKind.EMPTY -> HiddenKind.Empty
            ScanKind.TEMP -> HiddenKind.Temp
        },
        path = path,
        sizeLabel = if (kind == ScanKind.EMPTY) "kosong" else formatBytes(sizeBytes),
        safeToDelete = safe,
    )
}
