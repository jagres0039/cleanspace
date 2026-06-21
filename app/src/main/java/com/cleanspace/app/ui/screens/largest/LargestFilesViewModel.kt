package com.cleanspace.app.ui.screens.largest

import android.content.Context
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.scan.ScannedFile
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
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
) : ViewModel() {

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
            runCatching { repo.largeFiles() }
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
            val uris = scanned.filter { it.id.toString() in idSet }.map { it.uri }
            if (uris.isEmpty()) return@launch
            val sender = repo.trashOrDelete(uris)
            if (sender != null) _confirm.emit(sender) else load()
        }
    }

    /** Called after the system confirmation dialog completes successfully. */
    fun onConfirmed() = load()
}
