package com.cleanspace.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.CsText
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

/** Brand gradient used by the logo, primary CTA and progress ring. */
val BrandGradient: Brush = Brush.linearGradient(
    0.0f to CsPalette.BrandGreenLight,
    0.52f to CsPalette.BrandGreen,
    1.0f to CsPalette.BrandGreenDark,
)

/** UPPERCASE section header used above grouped content. */
@Composable
fun CsSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = CsText.sectionLabel,
        color = MaterialTheme.colorsExt.textFaint,
        modifier = modifier.padding(start = Dimens.space4, bottom = Dimens.space8),
    )
}

/** Bordered surface container (the `.card` pattern). */
@Composable
fun CsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radiusCard),
        color = MaterialTheme.colorsExt.surface,
        border = BorderStroke(Dimens.borderHairline, MaterialTheme.colorsExt.border),
    ) { content() }
}

enum class CsButtonStyle { Primary, Secondary, Danger }

/** Full-width call-to-action button matching the prototype CTA styles. */
@Composable
fun CsButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CsButtonStyle = CsButtonStyle.Primary,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val ext = MaterialTheme.colorsExt
    val shape = RoundedCornerShape(Dimens.radiusButton)
    val base = Modifier
        .fillMaxWidth()
        .heightIn(min = Dimens.touchTargetMin)
        .clip(shape)

    val bg: Modifier = when (style) {
        CsButtonStyle.Primary -> base.background(BrandGradient)
        CsButtonStyle.Secondary -> base.background(ext.surfaceHover)
        CsButtonStyle.Danger -> base.background(ext.danger)
    }
    val content = when (style) {
        CsButtonStyle.Secondary -> ext.textSoft
        else -> Color.White
    }

    Row(
        modifier = bg
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Dimens.space16, vertical = Dimens.space14),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = content, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(Dimens.space8))
        }
        Text(label, style = CsText.buttonLabel, color = content)
    }
}

/** Tinted square icon badge (the `.thumb`/category swatch). */
@Composable
fun CsIconBadge(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = Dimens.rowThumb,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(Dimens.radiusChip))
            .background(tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

/** A tappable list row: badge + title/subtitle + trailing slot. */
@Composable
fun CsListRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val ext = MaterialTheme.colorsExt
    val base = modifier.fillMaxWidth().let { if (onClick != null) it.clickable(onClick = onClick) else it }
    Row(
        modifier = base.padding(horizontal = Dimens.space14, vertical = Dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CsIconBadge(icon = icon, tint = iconTint)
        Spacer(Modifier.size(Dimens.space12))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = ext.textSoft, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.size(Dimens.space12))
            trailing()
        }
    }
}

/** Callout banner (info / warn / danger / success). */
enum class CsCalloutTone { Info, Warn, Danger, Success }

@Composable
fun CsCallout(
    text: String,
    tone: CsCalloutTone = CsCalloutTone.Info,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val ext = MaterialTheme.colorsExt
    val accent = when (tone) {
        CsCalloutTone.Info -> ext.info
        CsCalloutTone.Warn -> ext.warn
        CsCalloutTone.Danger -> ext.danger
        CsCalloutTone.Success -> ext.success
    }
    val resolvedIcon = icon ?: when (tone) {
        CsCalloutTone.Info -> CsIcons.Info
        CsCalloutTone.Warn -> CsIcons.Zap
        CsCalloutTone.Danger -> CsIcons.Trash
        CsCalloutTone.Success -> CsIcons.ShieldCheck
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusCard))
            .background(accent.copy(alpha = 0.12f))
            .padding(Dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(resolvedIcon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(Dimens.space10))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Rounded check box matching the multi-select pattern. */
@Composable
fun CsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MaterialTheme.colorsExt
    Box(
        modifier = modifier
            .size(Dimens.checkbox)
            .clip(RoundedCornerShape(Dimens.radiusCheckbox))
            .background(if (checked) Color.Transparent else ext.surfaceHover)
            .let { if (checked) it.background(BrandGradient, RoundedCornerShape(Dimens.radiusCheckbox)) else it }
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(CsIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

/** Two+ option segmented control (the `.seg` pattern). */
@Composable
fun CsSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MaterialTheme.colorsExt
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusChip))
            .background(ext.surfaceHover)
            .padding(Dimens.space2),
    ) {
        options.forEachIndexed { i, label ->
            val selected = i == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(Dimens.space6))
                    .background(if (selected) ext.surface else Color.Transparent)
                    .clickable { onSelect(i) }
                    .padding(vertical = Dimens.space8),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) MaterialTheme.colorScheme.onSurface else ext.textSoft,
                )
            }
        }
    }
}
