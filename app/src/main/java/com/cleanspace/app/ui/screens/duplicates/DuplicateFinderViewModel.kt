package com.cleanspace.app.ui.screens.duplicates

import android.content.Context
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.scan.DuplicateSet
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.common.toGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI payload: rendered groups plus the total reclaimable label. */
data class DuplicateUi(
    val groups: List<DuplicateGroup>,
    val reclaimableLabel: String,
)

@HiltViewModel
class DuplicateFinderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState<DuplicateUi>>(ScanUiState.Loading)
    val state = _state.asStateFlow()

    private val _confirm = MutableSharedFlow<IntentSender>(extraBufferCapacity = 1)
    val confirm = _confirm.asSharedFlow()

    private var sets: List<DuplicateSet> = emptyList()

    init { load() }

    fun load() {
        viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            _state.value = ScanUiState.Loading
            runCatching { repo.duplicateSets() }
                .onSuccess { found ->
                    sets = found
                    val groups = found.map { it.toGroup(repo.keeperOf(it).id) }
                    val reclaim = found.sumOf { it.reclaimableBytes }
                    _state.value = ScanUiState.Ready(DuplicateUi(groups, formatBytes(reclaim)))
                }
                .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal memindai") }
        }
    }

    fun cleanAll() {
        viewModelScope.launch {
            val uris = sets.flatMap { set ->
                val keeperId = repo.keeperOf(set).id
                set.files.filter { it.id != keeperId }.map { it.uri }
            }
            if (uris.isEmpty()) return@launch
            // Wrap the delete-request build: some OEM ROMs throw here, and an
            // uncaught error in this coroutine would crash + restart the app
            // (looked like "kembali ke home"). On failure we show an error instead.
            runCatching { repo.trashOrDelete(uris) }
                .onSuccess { sender ->
                    if (sender != null) _confirm.emit(sender) else load()
                }
                .onFailure {
                    _state.value = ScanUiState.Error(
                        it.message ?: "Gagal menghapus duplikat. Coba lagi.",
                    )
                }
        }
    }

    fun onConfirmed() = load()
}
