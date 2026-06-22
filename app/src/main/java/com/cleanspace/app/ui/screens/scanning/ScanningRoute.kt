package com.cleanspace.app.ui.screens.scanning

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.common.ScanMessage
import com.cleanspace.app.ui.icons.CsIcons

@Composable
fun ScanningRoute(
    onFinished: (freedBytes: Long, fileCount: Int) -> Unit,
    onBack: () -> Unit,
    vm: DeepCleanViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        vm.onConfirmed(result.resultCode == Activity.RESULT_OK)
    }

    LaunchedEffect(Unit) {
        vm.confirm.collect { sender ->
            launcher.launch(IntentSenderRequest.Builder(sender).build())
        }
    }

    when (val s = state) {
        is DeepCleanUi.NeedsPermission -> ScanMessage(
            title = "Deep Clean",
            message = "Butuh izin akses media buat menjalankan Deep Clean.",
            icon = CsIcons.ShieldCheck,
            onBack = onBack,
        )
        is DeepCleanUi.Error -> ScanMessage(
            title = "Deep Clean",
            message = s.message,
            actionLabel = "Coba lagi",
            onAction = { vm.start() },
            onBack = onBack,
        )
        is DeepCleanUi.Scanning -> ScanningScreen(state = s.state)
        is DeepCleanUi.Done -> LaunchedEffect(s) { onFinished(s.freedBytes, s.fileCount) }
    }
}
