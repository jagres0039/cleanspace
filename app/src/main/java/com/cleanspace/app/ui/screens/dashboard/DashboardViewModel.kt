package com.cleanspace.app.ui.screens.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState<DashboardState>>(ScanUiState.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            _state.value = ScanUiState.Loading
            runCatching { repo.dashboard() }
                .onSuccess { _state.value = ScanUiState.Ready(it.toState()) }
                .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal memindai") }
        }
    }

    private fun ScanRepository.DashboardData.toState(): DashboardState {
        val recs = buildList {
            if (junkAndCacheBytes > 0) add(
                Recommendation("junk", CsIcons.Trash, CsPalette.Chart5, "Sampah & cache", "$junkCount file temp, thumbnail & cache aplikasi", formatBytes(junkAndCacheBytes)),
            )
            if (duplicateBytes > 0) add(
                Recommendation("dup", CsIcons.Copy, CsPalette.Chart8, "File duplikat", "$duplicateCount file kembar", formatBytes(duplicateBytes)),
            )
            if (whatsAppBytes > 0) add(
                Recommendation("wa", CsIcons.MessageCircle, CsPalette.Chart3, "WhatsApp media", "Foto, video, voice note", formatBytes(whatsAppBytes)),
            )
            if (largeBytes > 0) add(
                Recommendation("large", CsIcons.Box, CsPalette.Chart1, "File besar", "$largeCount file di atas 100 MB", formatBytes(largeBytes)),
            )
        }
        return DashboardState(
            usedLabel = formatBytes(summary.usedBytes),
            totalLabel = "dari ${formatBytes(summary.totalBytes)}",
            usedFraction = summary.usedFraction,
            reclaimableLabel = formatBytes(reclaimableBytes),
            recommendations = recs,
        )
    }
}
