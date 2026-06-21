package com.cleanspace.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsListRow
import com.cleanspace.app.ui.components.CsSectionLabel
import com.cleanspace.app.ui.components.StorageRing
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

/** A single cleanup opportunity surfaced on the dashboard. */
data class Recommendation(
    val id: String,
    val icon: ImageVector,
    val tint: Color,
    val title: String,
    val subtitle: String,
    val reclaimable: String,
)

/** Stateless dashboard UI state. */
data class DashboardState(
    val usedLabel: String,
    val totalLabel: String,
    val usedFraction: Float,
    val reclaimableLabel: String,
    val recommendations: List<Recommendation>,
)

@Composable
fun DashboardScreen(
    state: DashboardState,
    modifier: Modifier = Modifier,
    onRecommendationClick: (Recommendation) -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.screenPaddingH,
            end = Dimens.screenPaddingH,
            top = Dimens.space12,
            bottom = Dimens.space24,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.space16),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.space8),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                StorageRing(
                    fraction = state.usedFraction,
                    usedLabel = state.usedLabel,
                    totalLabel = state.totalLabel,
                )
                Spacer(Modifier.height(Dimens.space12))
                Text(
                    text = "${state.reclaimableLabel} bisa dibersihkan",
                    style = MaterialTheme.typography.titleSmall,
                    color = CsPalette.BrandGreen,
                    textAlign = TextAlign.Center,
                )
            }
        }

        item { CsSectionLabel("Rekomendasi") }

        items(state.recommendations, key = { it.id }) { rec ->
            CsCard {
                CsListRow(
                    icon = rec.icon,
                    iconTint = rec.tint,
                    title = rec.title,
                    subtitle = rec.subtitle,
                    onClick = { onRecommendationClick(rec) },
                    trailing = {
                        Text(
                            rec.reclaimable,
                            style = MaterialTheme.typography.titleSmall,
                            color = ext.textSoft,
                        )
                    },
                )
            }
        }
    }
}

/** Sample data for previews and early wiring. */
fun sampleDashboardState(): DashboardState = DashboardState(
    usedLabel = "82.4 GB",
    totalLabel = "dari 128 GB",
    usedFraction = 0.644f,
    reclaimableLabel = "12.8 GB",
    recommendations = listOf(
        Recommendation("dup", CsIcons.Copy, CsPalette.Chart8, "File duplikat", "428 file kembar", "3.2 GB"),
        Recommendation("wa", CsIcons.MessageCircle, CsPalette.Chart3, "WhatsApp media", "Foto, video, voice note", "5.1 GB"),
        Recommendation("large", CsIcons.Box, CsPalette.Chart1, "File besar", "Lebih dari 100 MB", "2.9 GB"),
        Recommendation("video", CsIcons.Film, CsPalette.Chart5, "Video lama", "Belum dibuka 90 hari", "1.6 GB"),
    ),
)

@Preview(showBackground = true)
@Composable
private fun DashboardPreviewLight() {
    CleanSpaceTheme(darkTheme = false) { DashboardScreen(sampleDashboardState()) }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreviewDark() {
    CleanSpaceTheme(darkTheme = true) { DashboardScreen(sampleDashboardState()) }
}
