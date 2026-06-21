package com.cleanspace.app.ui.screens.hidden

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsCallout
import com.cleanspace.app.ui.components.CsCalloutTone
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsCheckbox
import com.cleanspace.app.ui.components.CsListRow
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

enum class HiddenKind(val label: String, val icon: ImageVector, val color: Color) {
    Thumbnails("Thumbnail cache", CsIcons.Image, CsPalette.Chart6),
    Leftover("Sisa aplikasi", CsIcons.Box, CsPalette.Chart2),
    Empty("Folder kosong", CsIcons.Trash, CsPalette.Chart8),
    Temp("File sementara", CsIcons.FileText, CsPalette.Chart1),
}

data class HiddenFolder(
    val id: String,
    val kind: HiddenKind,
    val path: String,
    val sizeLabel: String,
    val safeToDelete: Boolean,
)

@Composable
fun HiddenFoldersScreen(
    folders: List<HiddenFolder>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onDelete: (List<String>) -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    // safe items pre-selected
    val selected = remember {
        mutableStateMapOf<String, Boolean>().apply {
            folders.forEach { put(it.id, it.safeToDelete) }
        }
    }
    val selectedIds = selected.filterValues { it }.keys.toList()
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            CsTopBar(title = "Folder tersembunyi", onBack = onBack, actionIcon = CsIcons.Refresh, onAction = {})
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = Dimens.screenPaddingH,
                    end = Dimens.screenPaddingH,
                    top = Dimens.space8,
                    bottom = 96.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.space8),
            ) {
                item {
                    CsCallout(
                        text = "Folder yang ditandai aman udah dipilih otomatis. Folder berisiko dibiarkan kosong — cek dulu sebelum hapus.",
                        tone = CsCalloutTone.Info,
                    )
                    Spacer(Modifier.size(Dimens.space8))
                }
                items(folders, key = { it.id }) { folder ->
                    val isChecked = selected[folder.id] == true
                    CsCard {
                        CsListRow(
                            icon = folder.kind.icon,
                            iconTint = folder.kind.color,
                            title = folder.kind.label,
                            subtitle = folder.path,
                            onClick = { selected[folder.id] = !isChecked },
                            trailing = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(folder.sizeLabel, style = MaterialTheme.typography.titleSmall, color = ext.textSoft)
                                    Spacer(Modifier.size(Dimens.space10))
                                    CsCheckbox(checked = isChecked, onCheckedChange = { selected[folder.id] = it })
                                }
                            },
                        )
                    }
                }
            }
        }
        if (selectedIds.isNotEmpty()) {
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(ext.surface)
                    .padding(Dimens.space16),
            ) {
                CsButton(
                    label = "Hapus ${selectedIds.size} folder",
                    onClick = { onDelete(selectedIds) },
                    style = CsButtonStyle.Danger,
                    leadingIcon = CsIcons.Trash,
                )
            }
        }
    }
}

fun sampleHiddenFolders(): List<HiddenFolder> = listOf(
    HiddenFolder("1", HiddenKind.Thumbnails, "/DCIM/.thumbnails", "1.1 GB", true),
    HiddenFolder("2", HiddenKind.Leftover, "/Android/data/com.olddapp", "320 MB", true),
    HiddenFolder("3", HiddenKind.Temp, "/Download/.tmp", "96 MB", true),
    HiddenFolder("4", HiddenKind.Empty, "/Pictures/EmptyAlbum", "0 KB", true),
    HiddenFolder("5", HiddenKind.Leftover, "/Android/obb/com.game.legacy", "540 MB", false),
)

@Preview(showBackground = true)
@Composable
private fun HiddenFoldersPreview() {
    CleanSpaceTheme { HiddenFoldersScreen(sampleHiddenFolders()) }
}
