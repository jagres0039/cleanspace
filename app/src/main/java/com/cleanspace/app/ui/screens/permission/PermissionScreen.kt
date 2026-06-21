package com.cleanspace.app.ui.screens.permission

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.BrandGradient
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

private data class PermPoint(val icon: ImageVector, val title: String, val body: String)

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    onGrant: () -> Unit = {},
    onLearnMore: () -> Unit = {},
) {
    val ext = MaterialTheme.colorsExt
    val points = listOf(
        PermPoint(CsIcons.Search, "Pindai penyimpanan", "Lihat file besar, duplikat, dan media WhatsApp yang menuh-menuhin memori."),
        PermPoint(CsIcons.ShieldCheck, "100% di perangkat", "Semua scan jalan lokal di HP kamu. Nggak ada file yang diunggah ke mana pun."),
        PermPoint(CsIcons.Trash, "Hapus dengan aman", "File masuk Trash dulu 30 hari, jadi bisa dipulihkan kalau kehapus nggak sengaja."),
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.space24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(72.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BrandGradient),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CsIcons.Sparkles, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(Dimens.space24))
        Text(
            "Bersihin ruang penyimpanan",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimens.space8))
        Text(
            "CleanSpace butuh akses ke file & media buat menganalisis apa yang bisa dibersihkan.",
            style = MaterialTheme.typography.bodyMedium,
            color = ext.textSoft,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimens.space24))
        points.forEach { p ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.space10),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.tileIcon)
                        .clip(RoundedCornerShape(Dimens.radiusChip))
                        .background(ext.success.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(p.icon, contentDescription = null, tint = ext.success, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.size(Dimens.space12))
                Column(Modifier.weight(1f)) {
                    Text(p.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text(p.body, style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        CsButton("Izinkan akses", onClick = onGrant, leadingIcon = CsIcons.ShieldCheck)
        Spacer(Modifier.height(Dimens.space10))
        CsButton("Pelajari dulu", onClick = onLearnMore, style = CsButtonStyle.Secondary)
        Spacer(Modifier.height(Dimens.space24))
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionPreview() {
    CleanSpaceTheme { PermissionScreen() }
}
