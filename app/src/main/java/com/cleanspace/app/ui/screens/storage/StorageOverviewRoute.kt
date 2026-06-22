package com.cleanspace.app.ui.screens.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.common.ScanLoading
import com.cleanspace.app.ui.common.ScanMessage
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons

@Composable
fun StorageOverviewRoute(
    onBack: () -> Unit,
    onCategoryClick: (StorageCategory) -> Unit,
    vm: StorageOverviewViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    when (val s = state) {
        is ScanUiState.Loading -> ScanLoading("Penyimpanan", "Menghitung penggunaan penyimpanan…", onBack)
        is ScanUiState.NeedsPermission -> ScanMessage(
            title = "Penyimpanan",
            message = "Butuh izin akses media buat menghitung rincian kategori.",
            icon = CsIcons.ShieldCheck,
            onBack = onBack,
        )
        is ScanUiState.Error -> ScanMessage(
            title = "Penyimpanan",
            message = s.message,
            actionLabel = "Coba lagi",
            onAction = { vm.load() },
            onBack = onBack,
        )
        is ScanUiState.Ready -> StorageOverviewScreen(
            state = s.data,
            onBack = onBack,
            onCategoryClick = onCategoryClick,
        )
    }
}
