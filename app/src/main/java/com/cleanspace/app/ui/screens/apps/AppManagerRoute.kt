package com.cleanspace.app.ui.screens.apps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.common.ScanLoading
import com.cleanspace.app.ui.common.ScanMessage
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons

@Composable
fun AppManagerRoute(
    onBack: () -> Unit,
    onOpenAppSettings: (String) -> Unit,
    onUninstall: (String) -> Unit,
    vm: AppManagerViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    when (val s = state) {
        is ScanUiState.Loading -> ScanLoading("Kelola aplikasi", "Menghitung cache & ukuran aplikasi…", onBack)
        is ScanUiState.NeedsPermission -> ScanMessage(
            title = "Kelola aplikasi",
            message = "Butuh izin akses penggunaan buat melihat ukuran cache.",
            icon = CsIcons.ShieldCheck,
            onBack = onBack,
        )
        is ScanUiState.Error -> ScanMessage(
            title = "Kelola aplikasi",
            message = s.message,
            actionLabel = "Coba lagi",
            onAction = { vm.load() },
            onBack = onBack,
        )
        is ScanUiState.Ready -> AppManagerScreen(
            apps = s.data.apps,
            onBack = onBack,
            onOpenAppSettings = onOpenAppSettings,
            onUninstall = onUninstall,
        )
    }
}
