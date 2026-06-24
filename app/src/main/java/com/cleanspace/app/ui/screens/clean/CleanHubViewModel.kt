package com.cleanspace.app.ui.screens.clean

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.data.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the "Bersihkan" hub with REAL scanned sizes (previously the tool sizes
 * were hard-coded, so they never changed after cleaning). Reuses the dashboard
 * scan for duplicates/WhatsApp/large/junk and the app scan for clearable cache.
 */
@HiltViewModel
class CleanHubViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ScanRepository,
) : ViewModel() {

    data class CleanSizes(
        /** False until the first scan finishes, so the UI can show a placeholder. */
        val ready: Boolean = false,
        val duplicateBytes: Long = 0L,
        val whatsAppBytes: Long = 0L,
        val largeBytes: Long = 0L,
        val hiddenBytes: Long = 0L,
        val appsCacheBytes: Long = 0L,
    )

    private val _state = MutableStateFlow(CleanSizes())
    val state = _state.asStateFlow()

    // Prevents overlapping scans when the screen is resumed repeatedly.
    private var loadJob: Job? = null

    init { load() }

    fun load() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (!CsPermissions.hasMediaAccess(context)) return@launch
            // Duplicates + WhatsApp + large files + safe hidden junk in one scan.
            runCatching { repo.dashboard() }.onSuccess { d ->
                _state.value = _state.value.copy(
                    ready = true,
                    duplicateBytes = d.duplicateBytes,
                    whatsAppBytes = d.whatsAppBytes,
                    largeBytes = d.largeBytes,
                    hiddenBytes = d.junkBytes,
                )
            }
            // Clearable app cache (needs Usage Access; unknown sizes are -1 -> ignored).
            runCatching { repo.installedApps() }.onSuccess { apps ->
                _state.value = _state.value.copy(
                    ready = true,
                    appsCacheBytes = apps.sumOf { it.cacheBytes.coerceAtLeast(0L) },
                )
            }
        }
    }
}
