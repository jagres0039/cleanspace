package com.cleanspace.app.ui.screens.largest

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.common.ScanLoading
import com.cleanspace.app.ui.common.ScanMessage
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons

@Composable
fun LargestFilesRoute(
    onBack: () -> Unit,
    vm: LargestFilesViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) vm.onConfirmed()
    }

    LaunchedEffect(Unit) {
        vm.confirm.collect { sender ->
            launcher.launch(IntentSenderRequest.Builder(sender).build())
        }
    }

    when (val s = state) {
        is ScanUiState.Loading -> ScanLoading("File besar", "Memindai file ≥ 100 MB…", onBack)
        is ScanUiState.NeedsPermission -> ScanMessage(
            title = "File besar",
            message = "Butuh izin akses media buat memindai file besar.",
            icon = CsIcons.ShieldCheck,
            onBack = onBack,
        )
        is ScanUiState.Error -> ScanMessage(
            title = "File besar",
            message = s.message,
            actionLabel = "Coba lagi",
            onAction = { vm.load() },
            onBack = onBack,
        )
        is ScanUiState.Ready -> {
            if (s.data.isEmpty()) {
                ScanMessage(
                    title = "File besar",
                    message = "Nggak ada file di atas 100 MB. Mantap, storage kamu rapi! 🎉",
                    icon = CsIcons.CheckCircle,
                    onBack = onBack,
                )
            } else {
                LargestFilesScreen(
                    files = s.data,
                    onBack = onBack,
                    onDelete = { ids -> vm.delete(ids) },
                )
            }
        }
    }
}
