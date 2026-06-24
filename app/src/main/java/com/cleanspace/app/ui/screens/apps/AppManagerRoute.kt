package com.cleanspace.app.ui.screens.apps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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

    // Re-scan when returning to this screen (e.g. after clearing cache or
    // uninstalling via Android's official system pages). The previous list stays
    // visible while it refreshes so the sizes update without a jarring reload.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.load()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
