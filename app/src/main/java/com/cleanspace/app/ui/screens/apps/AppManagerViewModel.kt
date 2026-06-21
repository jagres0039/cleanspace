package com.cleanspace.app.ui.screens.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.data.ScanRepository
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.common.toEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI payload: app rows + whether Usage Access is granted (drives the hint). */
data class AppManagerUi(
    val apps: List<AppEntry>,
    val hasUsageAccess: Boolean,
)

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState<AppManagerUi>>(ScanUiState.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = ScanUiState.Loading
            runCatching { repo.installedApps() }
                .onSuccess { apps ->
                    _state.value = ScanUiState.Ready(
                        AppManagerUi(
                            apps = apps.map { it.toEntry() },
                            hasUsageAccess = CsPermissions.hasUsageAccess(context),
                        ),
                    )
                }
                .onFailure { _state.value = ScanUiState.Error(it.message ?: "Gagal memuat aplikasi") }
        }
    }
}
