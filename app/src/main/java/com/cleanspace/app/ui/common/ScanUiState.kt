package com.cleanspace.app.ui.common

/** Generic UI state for any scan-backed screen. */
sealed interface ScanUiState<out T> {
    /** Scan in progress. */
    data object Loading : ScanUiState<Nothing>

    /** Required permission missing; screen should prompt the user. */
    data object NeedsPermission : ScanUiState<Nothing>

    /** Scan finished with data (which may itself be empty). */
    data class Ready<T>(val data: T) : ScanUiState<T>

    /** Scan failed. */
    data class Error(val message: String) : ScanUiState<Nothing>
}
