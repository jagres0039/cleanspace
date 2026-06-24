package com.cleanspace.app.ui.screens.whatsapp

import android.content.Context
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.scan.ScannedFile
import com.cleanspace.app.core.scan.WhatsAppScanner.WaBucket
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.core.util.formatRelativeTime
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhatsAppViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState<List<WaMediaItem>>>(ScanUiState.Loading)
    val state = _state.asStateFlow()

    private val _confirm = MutableSharedFlow<IntentSender>(extraBufferCapacity = 1)
    val confirm = _confirm.asSharedFlow()

    // True while a delete is running so the UI can show the "Menghapus\u2026" overlay.
    private val _deleting = MutableStateFlow(false)
    val deleting = _deleting.asStateFlow()

    private var scanned: List<ScannedFile> = emptyList()

    init { load() }

    fun load() {
        viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            _state.value = ScanUiState.Loading
            runCatching { repo.whatsAppGroups() }
                .onSuccess { groups ->
                    scanned = groups.flatMap { it.files }
                    val items = groups.flatMap { g -> g.files.map { it.toItem(g.bucket) } }
                    _state.value = ScanUiState.Ready(items)
                }
                .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal memindai WhatsApp") }
        }
    }

    fun delete(ids: List<String>) {
        viewModelScope.launch {
            val idSet = ids.toSet()
            val targets = scanned.filter { it.id.toString() in idSet }
            if (targets.isEmpty()) return@launch
            // Media \u2192 system trash; non-media (documents) \u2192 deleted by path.
            _deleting.value = true
            try {
                runCatching { repo.deleteScanned(targets) }
                    .onSuccess { outcome -> if (outcome.confirmRequest != null) _confirm.emit(outcome.confirmRequest) else load() }
                    .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal menghapus media") }
            } finally {
                _deleting.value = false
            }
        }
    }

    fun onConfirmed() = load()

    private fun ScannedFile.toItem(bucket: WaBucket): WaMediaItem {
        val type = bucket.toType()
        // Only photos & videos get a real thumbnail; others fall back to an icon.
        val preview = if (type == WaMediaType.Photo || type == WaMediaType.Video) uri.toString() else null
        return WaMediaItem(
            id = id.toString(),
            type = type,
            name = name,
            meta = "${bucket.label()} · ${formatRelativeTime(dateModifiedMillis)}",
            sizeLabel = formatBytes(sizeBytes),
            previewUri = preview,
            mime = mimeType,
        )
    }

    private fun WaBucket.toType(): WaMediaType = when (this) {
        WaBucket.VIDEO -> WaMediaType.Video
        WaBucket.VOICE, WaBucket.AUDIO -> WaMediaType.Voice
        WaBucket.DOCUMENTS -> WaMediaType.Docs
        WaBucket.IMAGES, WaBucket.STATUS, WaBucket.STICKERS, WaBucket.GIF, WaBucket.OTHER -> WaMediaType.Photo
    }

    private fun WaBucket.label(): String = when (this) {
        WaBucket.IMAGES -> "Foto"
        WaBucket.VIDEO -> "Video"
        WaBucket.VOICE -> "Voice note"
        WaBucket.AUDIO -> "Audio"
        WaBucket.DOCUMENTS -> "Dokumen"
        WaBucket.STATUS -> "Status"
        WaBucket.STICKERS -> "Stiker"
        WaBucket.GIF -> "GIF"
        WaBucket.OTHER -> "Lainnya"
    }
}
