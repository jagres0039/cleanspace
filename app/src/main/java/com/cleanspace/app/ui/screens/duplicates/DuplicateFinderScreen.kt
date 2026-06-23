package com.cleanspace.app.ui.screens.duplicates

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cleanspace.app.ui.components.BrandGradient
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsCallout
import com.cleanspace.app.ui.components.CsCalloutTone
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsCheckbox
import com.cleanspace.app.ui.components.CsSectionLabel
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

data class DuplicateItem(
    val id: String,
    val name: String,
    val meta: String,
    val isBest: Boolean,
    val previewUri: String? = null,
    val mime: String? = null,
)

data class DuplicateGroup(
    val id: String,
    val title: String,
    val sizeEachLabel: String,
    val items: List<DuplicateItem>,
)

@Composable
fun DuplicateFinderScreen(
    groups: List<DuplicateGroup>,
    reclaimableLabel: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCleanAll: () -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            CsTopBar(title = "File duplikat", onBack = onBack)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = Dimens.screenPaddingH,
                    end = Dimens.screenPaddingH,
                    top = Dimens.space8,
                    bottom = 96.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.space16),
            ) {
                item {
                    CsCallout(
                        text = "Salinan terbaik (resolusi/ukuran tertinggi) otomatis dipertahankan. Sisanya dipilih buat dihapus. Tap thumbnail buat lihat isinya.",
                        tone = CsCalloutTone.Success,
                        icon = CsIcons.Sparkles,
                    )
                }
                groups.forEach { group ->
                    item(key = group.id) {
                        Column {
                            CsSectionLabel("${group.title} · ${group.sizeEachLabel}/file")
                            CsCard {
                                Column {
                                    group.items.forEachIndexed { idx, item ->
                                        DuplicateRow(item)
                                        if (idx != group.items.lastIndex) {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = Dimens.space14)
                                                    .height(Dimens.borderHairline)
                                                    .background(ext.border),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(ext.surface)
                .padding(Dimens.space16),
        ) {
            CsButton(
                label = "Bersihkan duplikat · $reclaimableLabel",
                onClick = onCleanAll,
                style = CsButtonStyle.Danger,
                leadingIcon = CsIcons.Trash,
            )
        }
    }
}

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
private fun DuplicateRow(item: DuplicateItem) {
    val ext = MaterialTheme.colorsExt
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.previewUri != null) { openPreview(context, item.previewUri, item.mime) }
            .padding(horizontal = Dimens.space14, vertical = Dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.rowThumb)
                .clip(RoundedCornerShape(Dimens.radiusChip))
                .background(if (item.isBest) ext.success.copy(alpha = 0.14f) else ext.surfaceHover),
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
                Icon(
                    if (item.isBest) CsIcons.ShieldCheck else CsIcons.Image,
                    contentDescription = null,
                    tint = if (item.isBest) ext.success else ext.textSoft,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Spacer(Modifier.size(Dimens.space12))
        Column(Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(item.meta, style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
        }
        Spacer(Modifier.size(Dimens.space10))
        if (item.isBest) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.radiusChip))
                    .background(BrandGradient)
                    .padding(horizontal = Dimens.space8, vertical = Dimens.space4),
            ) {
                Text("Disimpan", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            CsCheckbox(checked = true, onCheckedChange = {})
        }
    }
}

fun sampleDuplicateGroups(): List<DuplicateGroup> = listOf(
    DuplicateGroup(
        "g1", "IMG_2043 (3 salinan)", "4.2 MB",
        listOf(
            DuplicateItem("a", "IMG_2043.jpg", "4032×3024 · asli", true),
            DuplicateItem("b", "IMG_2043 (1).jpg", "1920×1440 · WhatsApp", false),
            DuplicateItem("c", "IMG_2043-copy.jpg", "1280×960 · unduhan", false),
        ),
    ),
    DuplicateGroup(
        "g2", "invoice-mei (2 salinan)", "820 KB",
        listOf(
            DuplicateItem("d", "invoice-mei.pdf", "Dokumen asli", true),
            DuplicateItem("e", "invoice-mei (1).pdf", "Salinan unduhan", false),
        ),
    ),
)

@Preview(showBackground = true)
@Composable
private fun DuplicateFinderPreview() {
    CleanSpaceTheme { DuplicateFinderScreen(sampleDuplicateGroups(), reclaimableLabel = "3.2 GB") }
}
