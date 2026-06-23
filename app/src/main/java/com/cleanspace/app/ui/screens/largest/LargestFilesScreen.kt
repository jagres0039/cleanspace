package com.cleanspace.app.ui.screens.largest

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

data class LargeFile(
    val id: String,
    val icon: ImageVector,
    val tint: Color,
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
fun LargestFilesScreen(
    files: List<LargeFile>,
    modifier: Modifier = Modifier,
    title: String = "File besar",
    onBack: () -> Unit = {},
    onDelete: (List<String>) -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    val context = LocalContext.current
    val selected = remember { mutableStateMapOf<String, Boolean>() }
    val selectedIds = selected.filterValues { it }.keys.toList()
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            CsTopBar(title = title, onBack = onBack, actionIcon = CsIcons.Search, onAction = {})
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
                items(files, key = { it.id }) { f ->
                    val isChecked = selected[f.id] == true
                    CsCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selected[f.id] = !isChecked }
                                .padding(horizontal = Dimens.space14, vertical = Dimens.space12),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Dimens.rowThumb)
                                    .clip(RoundedCornerShape(Dimens.radiusChip))
                                    .background(ext.surfaceHover)
                                    .clickable(enabled = f.previewUri != null) { openPreview(context, f.previewUri, f.mime) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (f.previewUri != null) {
                                    AsyncImage(
                                        model = f.previewUri,
                                        contentDescription = f.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                } else {
                                    Icon(f.icon, contentDescription = null, tint = f.tint, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(Modifier.size(Dimens.space12))
                            Column(Modifier.weight(1f)) {
                                Text(f.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(f.meta, style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
                            }
                            Spacer(Modifier.size(Dimens.space10))
                            Text(f.sizeLabel, style = MaterialTheme.typography.titleSmall, color = ext.textSoft)
                            Spacer(Modifier.size(Dimens.space10))
                            CsCheckbox(checked = isChecked, onCheckedChange = { selected[f.id] = it })
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = selectedIds.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(ext.surface)
                    .padding(Dimens.space16),
            ) {
                CsButton(
                    label = "Hapus ${selectedIds.size} file",
                    onClick = { onDelete(selectedIds) },
                    style = CsButtonStyle.Danger,
                    leadingIcon = CsIcons.Trash,
                )
            }
        }
    }
}

fun sampleLargeFiles(): List<LargeFile> = listOf(
    LargeFile("1", CsIcons.Film, CsPalette.Chart5, "VID-20260612-trip.mp4", "Video · 12 Jun 2026", "1.2 GB"),
    LargeFile("2", CsIcons.Film, CsPalette.Chart5, "screen-record-001.mp4", "Video · 3 Mei 2026", "840 MB"),
    LargeFile("3", CsIcons.Box, CsPalette.Chart1, "backup-2026.zip", "Arsip · 1 Jan 2026", "610 MB"),
    LargeFile("4", CsIcons.Image, CsPalette.Chart6, "album-liburan.zip", "Arsip · 20 Apr 2026", "430 MB"),
    LargeFile("5", CsIcons.FileText, CsPalette.Chart2, "materi-webinar.pdf", "Dokumen · 8 Feb 2026", "180 MB"),
)

@Preview(showBackground = true)
@Composable
private fun LargestFilesPreview() {
    CleanSpaceTheme { LargestFilesScreen(sampleLargeFiles()) }
}
