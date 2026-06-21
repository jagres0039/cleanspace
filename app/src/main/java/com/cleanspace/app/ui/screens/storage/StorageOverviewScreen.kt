package com.cleanspace.app.ui.screens.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsListRow
import com.cleanspace.app.ui.components.CsSectionLabel
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

data class StorageCategory(
    val id: String,
    val icon: ImageVector,
    val color: Color,
    val name: String,
    val sizeLabel: String,
    val fraction: Float,
)

data class StorageOverviewState(
    val usedLabel: String,
    val freeLabel: String,
    val categories: List<StorageCategory>,
)

@Composable
private fun StackedBar(categories: List<StorageCategory>, modifier: Modifier = Modifier) {
    val ext = MaterialTheme.colorsExt
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.trackHeight)
            .clip(RoundedCornerShape(Dimens.trackHeight))
            .background(ext.surfaceHover),
    ) {
        categories.forEach { c ->
            Box(
                modifier = Modifier
                    .weight(c.fraction.coerceAtLeast(0.0001f))
                    .fillMaxSize()
                    .background(c.color),
            )
        }
    }
}

@Composable
fun StorageOverviewScreen(
    state: StorageOverviewState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCategoryClick: (StorageCategory) -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CsTopBar(title = "Penyimpanan", onBack = onBack, actionIcon = CsIcons.Refresh, onAction = {})
        LazyColumn(
            contentPadding = PaddingValues(
                start = Dimens.screenPaddingH,
                end = Dimens.screenPaddingH,
                top = Dimens.space8,
                bottom = Dimens.space24,
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.space16),
        ) {
            item {
                CsCard {
                    Column(Modifier.padding(Dimens.space16)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(state.usedLabel, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.weight(1f))
                            Text("${state.freeLabel} bebas", style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
                        }
                        Spacer(Modifier.height(Dimens.space12))
                        StackedBar(state.categories)
                    }
                }
            }
            item { CsSectionLabel("Kategori") }
            items(state.categories, key = { it.id }) { c ->
                CsCard {
                    CsListRow(
                        icon = c.icon,
                        iconTint = c.color,
                        title = c.name,
                        subtitle = "${(c.fraction * 100).toInt()}% dari yang terpakai",
                        onClick = { onCategoryClick(c) },
                        trailing = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(c.sizeLabel, style = MaterialTheme.typography.titleSmall, color = ext.textSoft)
                                Spacer(Modifier.size(Dimens.space4))
                                androidx.compose.material3.Icon(CsIcons.ChevronRight, contentDescription = null, tint = ext.textFaint, modifier = Modifier.size(18.dp))
                            }
                        },
                    )
                }
            }
        }
    }
}

fun sampleStorageOverview(): StorageOverviewState = StorageOverviewState(
    usedLabel = "82.4 GB terpakai",
    freeLabel = "45.6 GB",
    categories = listOf(
        StorageCategory("photo", CsIcons.Image, CsPalette.Chart6, "Foto", "28.1 GB", 0.34f),
        StorageCategory("video", CsIcons.Film, CsPalette.Chart5, "Video", "21.7 GB", 0.26f),
        StorageCategory("apps", CsIcons.Smartphone, CsPalette.Chart1, "Aplikasi", "18.3 GB", 0.22f),
        StorageCategory("wa", CsIcons.MessageCircle, CsPalette.Chart3, "WhatsApp", "9.1 GB", 0.11f),
        StorageCategory("other", CsIcons.Box, CsPalette.Chart2, "Lainnya", "5.2 GB", 0.07f),
    ),
)

@Preview(showBackground = true)
@Composable
private fun StorageOverviewPreview() {
    CleanSpaceTheme { StorageOverviewScreen(sampleStorageOverview()) }
}
