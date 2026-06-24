package com.cleanspace.app.ui.screens.duplicates

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.common.DeletingOverlay
import com.cleanspace.app.ui.common.ScanLoading
import com.cleanspace.app.ui.common.ScanMessage
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons

@Composable
fun DuplicateFinderRoute(
    onBack: () -> Unit,
    vm: DuplicateFinderViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val deleting by vm.deleting.collectAsState()

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

    Box(Modifier.fillMaxSize()) {
        when (val s = state) {
            is ScanUiState.Loading -> ScanLoading("File duplikat", "Mencari salinan kembar…", onBack)
            is ScanUiState.NeedsPermission -> ScanMessage(
                title = "File duplikat",
                message = "Butuh izin akses media buat mencari duplikat.",
                icon = CsIcons.ShieldCheck,
                onBack = onBack,
            )
            is ScanUiState.Error -> ScanMessage(
                title = "File duplikat",
                message = s.message,
                actionLabel = "Coba lagi",
                onAction = { vm.load() },
                onBack = onBack,
            )
            is ScanUiState.Ready -> {
                if (s.data.groups.isEmpty()) {
                    ScanMessage(
                        title = "File duplikat",
                        message = "Nggak ada file duplikat. Bersih! ✨",
                        icon = CsIcons.CheckCircle,
                        onBack = onBack,
                    )
                } else {
                    DuplicateFinderScreen(
                        groups = s.data.groups,
                        reclaimableLabel = s.data.reclaimableLabel,
                        onBack = onBack,
                        onCleanAll = { vm.cleanAll() },
                    )
                }
            }
        }
        DeletingOverlay(visible = deleting)
    }
}
