package com.cleanspace.app.ui.screens.scanning

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.BrandGradient
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.CsText
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

enum class ScanStepStatus { Done, Active, Pending }

data class ScanStep(val label: String, val status: ScanStepStatus)

data class ScanningState(
    val progress: Float,
    val currentPath: String,
    val steps: List<ScanStep>,
)

@Composable
fun ScanningScreen(
    state: ScanningState,
    modifier: Modifier = Modifier,
) {
    val ext = MaterialTheme.colorsExt
    val animated by animateFloatAsState(state.progress.coerceIn(0f, 1f), tween(600), label = "scanProgress")
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.space24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.6f))
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BrandGradient),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CsIcons.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(Dimens.space24))
        Text("${(animated * 100).toInt()}%", style = CsText.displayBig, color = MaterialTheme.colorScheme.onSurface)
        Text("Memindai penyimpanan…", style = MaterialTheme.typography.titleSmall, color = ext.textSoft)
        Spacer(Modifier.height(Dimens.space16))
        Box(
            Modifier
                .fillMaxWidth()
                .height(Dimens.trackHeight)
                .clip(RoundedCornerShape(Dimens.trackHeight))
                .background(ext.surfaceHover),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(animated)
                    .height(Dimens.trackHeight)
                    .clip(RoundedCornerShape(Dimens.trackHeight))
                    .background(BrandGradient),
            )
        }
        Spacer(Modifier.height(Dimens.space8))
        Text(
            state.currentPath,
            style = MaterialTheme.typography.bodySmall,
            color = ext.textFaint,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Spacer(Modifier.height(Dimens.space24))
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.space12)) {
            state.steps.forEach { StepRow(it) }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun StepRow(step: ScanStep) {
    val ext = MaterialTheme.colorsExt
    Row(verticalAlignment = Alignment.CenterVertically) {
        val tint = when (step.status) {
            ScanStepStatus.Done -> ext.success
            ScanStepStatus.Active -> CsPalette.BrandGreen
            ScanStepStatus.Pending -> ext.textFaint
        }
        val icon = when (step.status) {
            ScanStepStatus.Done -> CsIcons.CheckCircle
            ScanStepStatus.Active -> CsIcons.Refresh
            ScanStepStatus.Pending -> CsIcons.Info
        }
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(Dimens.space12))
        Text(
            step.label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (step.status == ScanStepStatus.Pending) ext.textFaint else MaterialTheme.colorScheme.onSurface,
        )
    }
}

fun sampleScanningState(): ScanningState = ScanningState(
    progress = 0.62f,
    currentPath = "/storage/emulated/0/DCIM/Camera…",
    steps = listOf(
        ScanStep("Memindai foto & video", ScanStepStatus.Done),
        ScanStep("Mendeteksi file duplikat", ScanStepStatus.Active),
        ScanStep("Menganalisis media WhatsApp", ScanStepStatus.Pending),
        ScanStep("Mencari file besar", ScanStepStatus.Pending),
    ),
)

@Preview(showBackground = true)
@Composable
private fun ScanningPreview() {
    CleanSpaceTheme { ScanningScreen(sampleScanningState()) }
}
