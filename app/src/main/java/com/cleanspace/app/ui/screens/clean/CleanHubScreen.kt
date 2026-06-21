package com.cleanspace.app.ui.screens.clean

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.CsCallout
import com.cleanspace.app.ui.components.CsCalloutTone
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsListRow
import com.cleanspace.app.ui.components.CsSectionLabel
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

data class CleanTool(
    val route: String,
    val icon: ImageVector,
    val tint: Color,
    val title: String,
    val subtitle: String,
    val reclaimable: String,
)

@Composable
fun CleanHubScreen(
    tools: List<CleanTool>,
    modifier: Modifier = Modifier,
    onToolClick: (CleanTool) -> Unit = {},
    onDeepClean: () -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CsTopBar(title = "Bersihkan")
        LazyColumn(
            contentPadding = PaddingValues(
                start = Dimens.screenPaddingH,
                end = Dimens.screenPaddingH,
                top = Dimens.space8,
                bottom = Dimens.space24,
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.space8),
        ) {
            item {
                CsCallout(
                    text = "Deep Clean memindai semua kategori sekaligus. Tonton 1 iklan singkat buat membukanya — gratis.",
                    tone = CsCalloutTone.Success,
                    icon = CsIcons.Sparkles,
                )
                Spacer(Modifier.size(Dimens.space4))
            }
            item {
                CsCard {
                    CsListRow(
                        icon = CsIcons.Sparkles,
                        iconTint = CsPalette.BrandGreen,
                        title = "Deep Clean",
                        subtitle = "Scan menyeluruh semua kategori",
                        onClick = onDeepClean,
                        trailing = {
                            Icon(CsIcons.ChevronRight, contentDescription = null, tint = ext.textFaint, modifier = Modifier.size(18.dp))
                        },
                    )
                }
            }
            item { CsSectionLabel("Tool pembersih") }
            items(tools, key = { it.route }) { tool ->
                CsCard {
                    CsListRow(
                        icon = tool.icon,
                        iconTint = tool.tint,
                        title = tool.title,
                        subtitle = tool.subtitle,
                        onClick = { onToolClick(tool) },
                        trailing = {
                            Text(tool.reclaimable, style = MaterialTheme.typography.titleSmall, color = ext.textSoft)
                        },
                    )
                }
            }
        }
    }
}

fun cleanTools(): List<CleanTool> = listOf(
    CleanTool("duplicates", CsIcons.Copy, CsPalette.Chart8, "File duplikat", "Salinan kembar, auto-keep terbaik", "3.2 GB"),
    CleanTool("whatsapp", CsIcons.MessageCircle, CsPalette.Chart3, "Media WhatsApp", "Foto, video, voice note", "5.1 GB"),
    CleanTool("largest", CsIcons.Box, CsPalette.Chart1, "File besar (>100 MB)", "File & data paling makan ruang", "2.9 GB"),
    CleanTool("hidden", CsIcons.Image, CsPalette.Chart6, "Folder tersembunyi", ".thumbnails, sisa app, file temp", "2.0 GB"),
    CleanTool("apps", CsIcons.Smartphone, CsPalette.Chart5, "Kelola aplikasi", "Cache & app jarang dipakai", "4.5 GB"),
)

@Preview(showBackground = true)
@Composable
private fun CleanHubPreview() {
    CleanSpaceTheme { CleanHubScreen(cleanTools()) }
}
