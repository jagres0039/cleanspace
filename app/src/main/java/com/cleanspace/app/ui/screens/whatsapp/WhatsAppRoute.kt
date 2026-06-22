package com.cleanspace.app.ui.screens.whatsapp

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cleanspace.app.ui.common.ScanLoading
import com.cleanspace.app.ui.common.ScanMessage
import com.cleanspace.app.ui.common.ScanUiState
import com.cleanspace.app.ui.icons.CsIcons

@Composable
fun WhatsAppRoute(
    onBack: () -> Unit,
    vm: WhatsAppViewModel = hiltViewModel(),
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
        is ScanUiState.Loading -> ScanLoading("WhatsApp", "Memindai media WhatsApp…", onBack)
        is ScanUiState.NeedsPermission -> ScanMessage(
            title = "WhatsApp",
            message = "Butuh izin akses media buat memindai folder WhatsApp.",
            icon = CsIcons.ShieldCheck,
            onBack = onBack,
        )
        is ScanUiState.Error -> ScanMessage(
            title = "WhatsApp",
            message = s.message,
            actionLabel = "Coba lagi",
            onAction = { vm.load() },
            onBack = onBack,
        )
        is ScanUiState.Ready -> {
            if (s.data.isEmpty()) {
                ScanMessage(
                    title = "WhatsApp",
                    message = "Nggak nemu media WhatsApp yang numpuk. Bersih! 🎉",
                    icon = CsIcons.CheckCircle,
                    onBack = onBack,
                )
            } else {
                WhatsAppCleanerScreen(
                    items = s.data,
                    onBack = onBack,
                    onClean = { ids -> vm.delete(ids) },
                )
            }
        }
    }
}
