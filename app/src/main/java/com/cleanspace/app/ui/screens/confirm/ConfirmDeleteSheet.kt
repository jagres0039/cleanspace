package com.cleanspace.app.ui.screens.confirm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsCallout
import com.cleanspace.app.ui.components.CsCalloutTone
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

data class DeleteSummary(
    val fileCount: Int,
    val totalSizeLabel: String,
)

/** Reusable confirmation content (also used directly inside the sheet). */
@Composable
fun ConfirmDeleteContent(
    summary: DeleteSummary,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.space24, vertical = Dimens.space8),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Hapus ${summary.fileCount} file?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimens.space4))
        Text(
            "Membebaskan ${summary.totalSizeLabel} ruang penyimpanan.",
            style = MaterialTheme.typography.bodyMedium,
            color = ext.textSoft,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimens.space16))
        CsCallout(
            text = "Aman: file dipindahkan ke Trash dan bisa dipulihkan dalam 30 hari sebelum dihapus permanen.",
            tone = CsCalloutTone.Success,
            icon = CsIcons.ShieldCheck,
        )
        Spacer(Modifier.height(Dimens.space16))
        CsButton(
            label = "Pindahkan ke Trash",
            onClick = onConfirm,
            style = CsButtonStyle.Danger,
            leadingIcon = CsIcons.Trash,
        )
        Spacer(Modifier.height(Dimens.space10))
        CsButton(label = "Batal", onClick = onCancel, style = CsButtonStyle.Secondary)
        Spacer(Modifier.height(Dimens.space16))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDeleteSheet(
    summary: DeleteSummary,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorsExt.surface,
    ) {
        ConfirmDeleteContent(
            summary = summary,
            onConfirm = onConfirm,
            onCancel = onDismiss,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmDeletePreview() {
    CleanSpaceTheme { ConfirmDeleteContent(DeleteSummary(fileCount = 428, totalSizeLabel = "3.2 GB")) }
}
