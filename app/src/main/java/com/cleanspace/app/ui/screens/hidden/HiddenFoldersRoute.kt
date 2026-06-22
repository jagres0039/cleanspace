package com.cleanspace.app.ui.screens.hidden

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.common.ScanLoading
import com.cleanspace.app.ui.common.ScanMessage
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons

@Composable
fun HiddenFoldersRoute(
    onBack: () -> Unit,
    vm: HiddenFoldersViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    when (val s = state) {
        is ScanUiState.Loading -> ScanLoading("Folder tersembunyi", "Memindai folder tersembunyi…", onBack)
        is ScanUiState.NeedsPermission -> ScanMessage(
            title = "Folder tersembunyi",
            message = "Butuh izin akses semua file (All files access) buat memindai folder tersembunyi.",
            icon = CsIcons.ShieldCheck,
            onBack = onBack,
        )
        is ScanUiState.Error -> ScanMessage(
            title = "Folder tersembunyi",
            message = s.message,
            actionLabel = "Coba lagi",
            onAction = { vm.load() },
            onBack = onBack,
        )
        is ScanUiState.Ready -> {
            if (s.data.isEmpty()) {
                ScanMessage(
                    title = "Folder tersembunyi",
                    message = "Nggak ada folder sampah tersembunyi. Storage kamu rapi! 🎉",
                    icon = CsIcons.CheckCircle,
                    onBack = onBack,
                )
            } else {
                HiddenFoldersScreen(
                    folders = s.data,
                    onBack = onBack,
                    onDelete = { ids -> vm.delete(ids) },
                )
            }
        }
    }
}
