package com.cleanspace.app.ui.screens.done

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.BrandGradient
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsListRow
import com.cleanspace.app.ui.components.CsSectionLabel
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.CsText
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

data class NextStep(
    val id: String,
    val icon: ImageVector,
    val tint: Color,
    val title: String,
    val subtitle: String,
)

@Composable
fun DoneScreen(
    freedLabel: String,
    filesRemovedLabel: String,
    nextSteps: List<NextStep>,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
    onNextStep: (NextStep) -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.screenPaddingH),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(64.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(BrandGradient),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CsIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
        }
        Spacer(Modifier.height(Dimens.space16))
        Text("Berhasil dibersihkan!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(Dimens.space4))
        Text(freedLabel, style = CsText.displayBig, color = CsPalette.BrandGreen)
        Text("$filesRemovedLabel dipindahkan ke Trash", style = MaterialTheme.typography.bodyMedium, color = ext.textSoft, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Dimens.space24))
        if (nextSteps.isNotEmpty()) {
            CsSectionLabel("Langkah berikutnya", modifier = Modifier.fillMaxWidth())
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.space8)) {
                nextSteps.forEach { step ->
                    CsCard {
                        CsListRow(
                            icon = step.icon,
                            iconTint = step.tint,
                            title = step.title,
                            subtitle = step.subtitle,
                            onClick = { onNextStep(step) },
                            trailing = {
                                Icon(CsIcons.ChevronRight, contentDescription = null, tint = ext.textFaint, modifier = Modifier.size(18.dp))
                            },
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        CsButton("Selesai", onClick = onDone, style = CsButtonStyle.Secondary)
        Spacer(Modifier.height(Dimens.space24))
    }
}

fun sampleNextSteps(): List<NextStep> = listOf(
    NextStep("apps", CsIcons.Smartphone, CsPalette.Chart1, "Cek aplikasi jarang dipakai", "Bisa hemat ~2.4 GB lagi"),
    NextStep("hidden", CsIcons.Box, CsPalette.Chart2, "Folder tersembunyi", "Sisa cache & .thumbnails"),
)

@Preview(showBackground = true)
@Composable
private fun DonePreview() {
    CleanSpaceTheme { DoneScreen(freedLabel = "12.8 GB", filesRemovedLabel = "512 file", nextSteps = sampleNextSteps()) }
}
