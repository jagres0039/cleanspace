package com.cleanspace.app.ui.screens.whatsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsCheckbox
import com.cleanspace.app.ui.components.CsSegmentedControl
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

enum class WaMediaType(val label: String, val icon: ImageVector, val color: Color) {
    Photo("Foto", CsIcons.Image, CsPalette.Chart6),
    Video("Video", CsIcons.Film, CsPalette.Chart5),
    Voice("Voice note", CsIcons.Mic, CsPalette.Chart1),
    Docs("Dokumen", CsIcons.FileText, CsPalette.Chart2),
}

data class WaMediaItem(
    val id: String,
    val type: WaMediaType,
    val name: String,
    val meta: String,
    val sizeLabel: String,
    val previewUri: String? = null,
    val mime: String? = null,
)

private fun openPreview(context: Context, uri: String?, mime: String?) {
    if (uri == null) return
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(uri), mime ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@Composable
fun WhatsAppCleanerScreen(
    items: List<WaMediaItem>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onClean: (List<String>) -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    val context = LocalContext.current
    val filters = listOf("Semua") + WaMediaType.values().map { it.label }
    var filterIndex by remember { mutableStateOf(0) }
    val selected = remember { androidx.compose.runtime.mutableStateMapOf<String, Boolean>() }

    val visible = if (filterIndex == 0) items else items.filter { it.type == WaMediaType.values()[filterIndex - 1] }
    val selectedIds = selected.filterValues { it }.keys.toList()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            CsTopBar(title = "WhatsApp", onBack = onBack)
            Box(Modifier.padding(horizontal = Dimens.screenPaddingH, vertical = Dimens.space8)) {
                CsSegmentedControl(
                    options = filters,
                    selectedIndex = filterIndex,
                    onSelect = { filterIndex = it },
                )
            }
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
                items(visible, key = { it.id }) { item ->
                    val isChecked = selected[item.id] == true
                    CsCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selected[item.id] = !isChecked }
                                .padding(horizontal = Dimens.space14, vertical = Dimens.space12),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Dimens.rowThumb)
                                    .clip(RoundedCornerShape(Dimens.radiusChip))
                                    .background(ext.surfaceHover)
                                    .clickable(enabled = item.previewUri != null) { openPreview(context, item.previewUri, item.mime) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (item.previewUri != null) {
                                    AsyncImage(
                                        model = item.previewUri,
                                        contentDescription = item.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                } else {
                                    Icon(item.type.icon, contentDescription = null, tint = item.type.color, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(Modifier.size(Dimens.space12))
                            Column(Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(item.meta, style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
                            }
                            Spacer(Modifier.size(Dimens.space10))
                            Text(item.sizeLabel, style = MaterialTheme.typography.titleSmall, color = ext.textSoft)
                            Spacer(Modifier.size(Dimens.space10))
                            CsCheckbox(checked = isChecked, onCheckedChange = { selected[item.id] = it })
                        }
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
                    label = "Hapus ${selectedIds.size} item",
                    onClick = { onClean(selectedIds) },
                    style = CsButtonStyle.Danger,
                    leadingIcon = CsIcons.Trash,
                )
            }
        }
    }
}

fun sampleWaMedia(): List<WaMediaItem> = listOf(
    WaMediaItem("1", WaMediaType.Video, "VID-grup-keluarga.mp4", "Grup Keluarga · hari ini", "96 MB"),
    WaMediaItem("2", WaMediaType.Photo, "IMG-pagi.jpg", "Grup RT · kemarin", "3.1 MB"),
    WaMediaItem("3", WaMediaType.Voice, "PTT-voice-note.opus", "Budi · 2 hari lalu", "1.4 MB"),
    WaMediaItem("4", WaMediaType.Photo, "IMG-stiker-pagi.jpg", "Grup Kantor · 3 hari lalu", "820 KB"),
    WaMediaItem("5", WaMediaType.Docs, "undangan.pdf", "Sari · 1 minggu lalu", "640 KB"),
    WaMediaItem("6", WaMediaType.Video, "VID-lucu.mp4", "Grup Alumni · 1 minggu lalu", "58 MB"),
)

@Preview(showBackground = true)
@Composable
private fun WhatsAppCleanerPreview() {
    CleanSpaceTheme { WhatsAppCleanerScreen(sampleWaMedia()) }
}
