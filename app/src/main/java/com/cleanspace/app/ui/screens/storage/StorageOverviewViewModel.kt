package com.cleanspace.app.ui.screens.storage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.scan.StorageScanner
import com.cleanspace.app.core.util.formatBytesSi
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.common.categoryLabel
import com.cleanspace.app.ui.common.iconFor
import com.cleanspace.app.ui.common.tintFor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageOverviewViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState<StorageOverviewState>>(ScanUiState.Loading)
    val state = _state.asStateFlow()

    // Tracks the in-flight scan so resume-triggered reloads don't stack up.
    private var loadJob: Job? = null

    init { load() }

    fun load() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) {
                _state.value = ScanUiState.NeedsPermission
                return@launch
            }
            // Keep showing the previous breakdown while refreshing (no spinner flash).
            if (_state.value !is ScanUiState.Ready) _state.value = ScanUiState.Loading
            runCatching { repo.storageSummary() }
                .onSuccess { _state.value = ScanUiState.Ready(it.toOverview()) }
                .onFailure {
                    if (_state.value !is ScanUiState.Ready) {
                        _state.value = ScanUiState.Error(it.message ?: "Gagal membaca penyimpanan")
                    }
                }
        }
    }

    private fun StorageScanner.StorageSummary.toOverview(): StorageOverviewState {
        val cats = byCategory.entries
            .filter { it.value > 0 }
            .sortedByDescending { it.value }
        val totalCat = cats.sumOf { it.value }.coerceAtLeast(1L)
        return StorageOverviewState(
            usedLabel = "${formatBytesSi(usedBytes)} terpakai",
            freeLabel = formatBytesSi(freeBytes),
            categories = cats.map { (cat, bytes) ->
                StorageCategory(
                    id = cat.name,
                    icon = iconFor(cat),
                    color = tintFor(cat),
                    name = categoryLabel(cat),
                    sizeLabel = formatBytesSi(bytes),
                    fraction = (bytes.toDouble() / totalCat).toFloat(),
                )
            },
        )
    }
}
