package com.cleanspace.app.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsIconBadge
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

/**
 * In-list native ad placeholder. Real native assets (headline, body, icon, CTA,
 * media) get bound here once an SDK is wired. Until then it renders a clearly
 * labelled "Bersponsor" card so layout/spacing is final and policy-safe
 * (ad attribution is always visible).
 */
@Composable
fun NativeAdSlot(
    modifier: Modifier = Modifier,
    headline: String = "Ruang lega, performa makin ngebut",
    body: String = "Tips & tools biar HP kamu tetap kenceng.",
    ctaText: String = "Pelajari",
) {
    val ext = MaterialTheme.colorsExt
    CsCard(modifier = modifier) {
        Column(Modifier.padding(Dimens.space14)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CsIconBadge(icon = CsIcons.Sparkles, tint = CsPalette.Chart1)
                Spacer(Modifier.size(Dimens.space12))
                Column(Modifier.weight(1f)) {
                    Text(headline, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                    Text(body, style = MaterialTheme.typography.bodySmall, color = ext.textSoft, maxLines = 1)
                }
                Spacer(Modifier.size(Dimens.space8))
                AdBadge()
            }
            Spacer(Modifier.size(Dimens.space12))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusButton))
                    .background(ext.surfaceHover)
                    .padding(vertical = Dimens.space10),
                contentAlignment = Alignment.Center,
            ) {
                Text(ctaText, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun AdBadge() {
    val ext = MaterialTheme.colorsExt
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.space4))
            .background(ext.warn.copy(alpha = 0.16f))
            .padding(horizontal = Dimens.space6, vertical = 2.dp),
    ) {
        Text("Bersponsor", style = MaterialTheme.typography.labelSmall, color = ext.warn, fontWeight = FontWeight.Bold)
    }
}
