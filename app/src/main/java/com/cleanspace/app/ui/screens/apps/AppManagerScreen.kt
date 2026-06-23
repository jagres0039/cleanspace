package com.cleanspace.app.ui.screens.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsCallout
import com.cleanspace.app.ui.components.CsCalloutTone
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsIconBadge
import com.cleanspace.app.ui.components.CsSectionLabel
import com.cleanspace.app.ui.components.CsSegmentedControl
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

data class AppEntry(
    val packageName: String,
    val name: String,
    val cacheLabel: String,
    val totalSizeLabel: String,
    val lastUsedLabel: String,
    val unused: Boolean,
    val tint: Color,
)

/**
 * App Manager (policy-compliant):
 * - "Cache & data" deep-links to each app's system App Info (Storage) page,
 *   where the user can clear cache AND clear data in one place. Android does not
 *   allow silently clearing another app's cache/data since API 23.
 * - "Uninstall" launches the official Android uninstall dialog.
 */
@Composable
fun AppManagerScreen(
    apps: List<AppEntry>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenAppSettings: (String) -> Unit = {},
    onUninstall: (String) -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    val tabs = listOf("Semua", "Jarang dipakai")
    var tab by remember { mutableStateOf(0) }
    val visible = if (tab == 1) apps.filter { it.unused } else apps

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CsTopBar(title = "Kelola aplikasi", onBack = onBack)
        Box(Modifier.padding(horizontal = Dimens.screenPaddingH, vertical = Dimens.space8)) {
            CsSegmentedControl(options = tabs, selectedIndex = tab, onSelect = { tab = it })
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
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
                    text = "Demi keamanan Android, cache & data app lain dibersihkan lewat halaman App Info resmi — CleanSpace mengarahkan kamu ke sana dalam sekali tap. Uninstall pakai dialog resmi Android.",
                    tone = CsCalloutTone.Info,
                )
                Spacer(Modifier.size(Dimens.space4))
            }
            if (tab == 1) item { CsSectionLabel("Belum dibuka 30+ hari") }
            items(visible, key = { it.packageName }) { app ->
                CsCard {
                    Column(Modifier.padding(Dimens.space14)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CsIconBadge(icon = CsIcons.Smartphone, tint = app.tint)
                            Spacer(Modifier.size(Dimens.space12))
                            Column(Modifier.weight(1f)) {
                                Text(app.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    "${app.totalSizeLabel} · cache ${app.cacheLabel} · ${app.lastUsedLabel}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ext.textSoft,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Spacer(Modifier.size(Dimens.space12))
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space8)) {
                            Box(Modifier.weight(1f)) {
                                CsButton(
                                    label = "Cache & data",
                                    onClick = { onOpenAppSettings(app.packageName) },
                                    style = CsButtonStyle.Secondary,
                                    leadingIcon = CsIcons.Trash,
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                CsButton(
                                    label = "Uninstall",
                                    onClick = { onUninstall(app.packageName) },
                                    style = if (app.unused) CsButtonStyle.Danger else CsButtonStyle.Secondary,
                                    leadingIcon = CsIcons.Box,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun sampleApps(): List<AppEntry> = listOf(
    AppEntry("com.game.legacy", "Legacy Racing 3D", "512 MB", "2.4 GB", "3 bln lalu", true, CsPalette.Chart5),
    AppEntry("com.shop.old", "ShopOld", "180 MB", "640 MB", "45 hari lalu", true, CsPalette.Chart1),
    AppEntry("com.social.app", "Sociogram", "1.1 GB", "3.8 GB", "hari ini", false, CsPalette.Chart6),
    AppEntry("com.chat.app", "ChatMax", "720 MB", "2.1 GB", "kemarin", false, CsPalette.Chart3),
)

@Preview(showBackground = true)
@Composable
private fun AppManagerPreview() {
    CleanSpaceTheme { AppManagerScreen(sampleApps()) }
}
