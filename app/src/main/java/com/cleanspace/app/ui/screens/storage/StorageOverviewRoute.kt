package com.cleanspace.app.ui.screens.storage

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
fun StorageOverviewRoute(
    onBack: () -> Unit,
    onCategoryClick: (StorageCategory) -> Unit,
    vm: StorageOverviewViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    // Re-scan whenever this screen becomes visible again (e.g. after deleting
    // files elsewhere). Previous numbers stay on screen while it refreshes.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.load()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
