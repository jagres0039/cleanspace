package com.cleanspace.app.ui.components

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
import com.cleanspace.app.ui.theme.neuInset
import com.cleanspace.app.ui.theme.neuRaised

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

/** Raised soft-UI surface container (the `.card` pattern). */
@Composable
fun CsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val ext = MaterialTheme.colorsExt
    val shape = RoundedCornerShape(Dimens.radiusCard)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neuRaised(
                backgroundColor = ext.neuBg,
                shadowDark = ext.neuShadowDark,
                shadowLight = ext.neuShadowLight,
                cornerRadius = Dimens.radiusCard,
                elevation = 6.dp,
            )
            .clip(shape),
    ) { content() }
}

enum class CsButtonStyle { Primary, Secondary, Danger }

/** Full-width call-to-action button, raised with the soft-UI shadows. */
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
    val raised = modifier
        .fillMaxWidth()
        .heightIn(min = Dimens.touchTargetMin)
        .neuRaised(
            backgroundColor = ext.neuBg,
            shadowDark = ext.neuShadowDark,
            shadowLight = ext.neuShadowLight,
            cornerRadius = Dimens.radiusButton,
            elevation = 5.dp,
        )
        .clip(shape)

    val filled = when (style) {
        CsButtonStyle.Primary -> raised.background(BrandGradient)
        CsButtonStyle.Danger -> raised.background(ext.danger)
        CsButtonStyle.Secondary -> raised
    }
    val content = when (style) {
        CsButtonStyle.Secondary -> MaterialTheme.colorScheme.onSurface
        else -> Color.White
    }

    Row(
        modifier = filled
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

/** Tinted square icon badge, rendered as a sunken soft-UI well. */
@Composable
fun CsIconBadge(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = Dimens.rowThumb,
) {
    val ext = MaterialTheme.colorsExt
    Box(
        modifier = modifier
            .size(size)
            .neuInset(
                backgroundColor = ext.neuBg,
                shadowDark = ext.neuShadowDark,
                shadowLight = ext.neuShadowLight,
                cornerRadius = Dimens.radiusChip,
                depth = 3.dp,
            ),
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
            .neuRaised(
                backgroundColor = ext.neuBg,
                shadowDark = ext.neuShadowDark,
                shadowLight = ext.neuShadowLight,
                cornerRadius = Dimens.radiusCard,
                elevation = 4.dp,
            )
            .clip(RoundedCornerShape(Dimens.radiusCard))
            .padding(Dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .neuInset(
                    backgroundColor = ext.neuBg,
                    shadowDark = ext.neuShadowDark,
                    shadowLight = ext.neuShadowLight,
                    cornerRadius = Dimens.radiusChip,
                    depth = 3.dp,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(resolvedIcon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.size(Dimens.space10))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Rounded check box: sunken when off, raised brand gradient when on. */
@Composable
fun CsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MaterialTheme.colorsExt
    val shape = RoundedCornerShape(Dimens.radiusCheckbox)
    val box = if (checked) {
        modifier
            .size(Dimens.checkbox)
            .neuRaised(
                backgroundColor = ext.neuBg,
                shadowDark = ext.neuShadowDark,
                shadowLight = ext.neuShadowLight,
                cornerRadius = Dimens.radiusCheckbox,
                elevation = 3.dp,
            )
            .clip(shape)
            .background(BrandGradient)
    } else {
        modifier
            .size(Dimens.checkbox)
            .neuInset(
                backgroundColor = ext.neuBg,
                shadowDark = ext.neuShadowDark,
                shadowLight = ext.neuShadowLight,
                cornerRadius = Dimens.radiusCheckbox,
                depth = 2.dp,
            )
    }
    Box(
        modifier = box.clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(CsIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

/** Two+ option segmented control: sunken track, raised selected pill. */
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
            .neuInset(
                backgroundColor = ext.neuBg,
                shadowDark = ext.neuShadowDark,
                shadowLight = ext.neuShadowLight,
                cornerRadius = Dimens.radiusChip,
                depth = 3.dp,
            )
            .padding(Dimens.space2),
    ) {
        options.forEachIndexed { i, label ->
            val selected = i == selectedIndex
            val segShape = RoundedCornerShape(Dimens.space6)
            val seg = Modifier
                .weight(1f)
                .let {
                    if (selected) it.neuRaised(
                        backgroundColor = ext.neuBg,
                        shadowDark = ext.neuShadowDark,
                        shadowLight = ext.neuShadowLight,
                        cornerRadius = Dimens.space6,
                        elevation = 3.dp,
                    ) else it
                }
                .clip(segShape)
                .clickable { onSelect(i) }
                .padding(vertical = Dimens.space8)
            Box(
                modifier = seg,
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) CsPalette.BrandGreen else ext.textSoft,
                )
            }
        }
    }
}
