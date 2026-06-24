package com.cleanspace.app.ui.screens.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.core.util.formatBytesSi
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
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

    // True while a manual "Pindai ulang" is running, so the dashboard can show a
    // spinner on the rescan button without blocking the rest of the UI.
    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    // Tracks the in-flight scan so resume-triggered reloads don't pile up
    // multiple heavy scans at once (which made the screen feel stuck).
    private var loadJob: Job? = null

    init { load() }

    fun load() {
        // Skip if a scan is already running; the next resume will refresh again.
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            // Keep showing the previous data while refreshing instead of flashing the spinner.
            if (_state.value !is ScanUiState.Ready) _state.value = ScanUiState.Loading
            // Phase 1 — fast glance so the dashboard paints almost immediately.
            runCatching { repo.quickGlance() }
                .onSuccess { _state.value = ScanUiState.Ready(it.toState()) }
                .onFailure {
                    if (_state.value !is ScanUiState.Ready) {
                        _state.value = ScanUiState.Error(it.message ?: "Gagal memindai")
                    }
                }
            // Phase 2 — full scan (duplicates + junk) refines the numbers in place.
            runCatching { repo.dashboard() }
                .onSuccess { _state.value = ScanUiState.Ready(it.toState()) }
        }
    }

    /**
     * Manual rescan triggered by the dashboard's "Pindai ulang" button. Cancels
     * any in-flight scan and forces a fresh full scan, surfacing a spinner via
     * [refreshing] while it runs. Previous numbers stay on screen meanwhile.
     */
    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            _refreshing.value = true
            try {
                runCatching { repo.quickGlance() }
                    .onSuccess { _state.value = ScanUiState.Ready(it.toState()) }
                runCatching { repo.dashboard() }
                    .onSuccess { _state.value = ScanUiState.Ready(it.toState()) }
            } finally {
                _refreshing.value = false
            }
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
            usedLabel = formatBytesSi(summary.usedBytes),
            totalLabel = "dari ${formatBytesSi(summary.totalBytes)}",
            usedFraction = summary.usedFraction,
            reclaimableLabel = formatBytes(reclaimableBytes),
            recommendations = recs,
        )
    }
}
