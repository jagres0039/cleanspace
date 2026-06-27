package com.cleanspace.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt
import com.cleanspace.app.ui.theme.neuRaised

/** Lightweight top app bar: optional back button, title, optional trailing action. */
@Composable
fun CsTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Dimens.space8, vertical = Dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            CsAppBarButton(CsIcons.ChevronLeft, onClick = onBack)
            Spacer(Modifier.size(Dimens.space4))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(horizontal = Dimens.space8),
        )
        if (actionIcon != null && onAction != null) {
            CsAppBarButton(actionIcon, onClick = onAction)
        }
    }
}

@Composable
private fun CsAppBarButton(icon: ImageVector, onClick: () -> Unit) {
    val ext = MaterialTheme.colorsExt
    Box(
        modifier = Modifier
            .size(Dimens.appbarIcon)
            .neuRaised(
                backgroundColor = ext.neuBg,
                shadowDark = ext.neuShadowDark,
                shadowLight = ext.neuShadowLight,
                cornerRadius = Dimens.radiusChip,
                elevation = 4.dp,
            )
            .clip(RoundedCornerShape(Dimens.radiusChip))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = ext.textSoft, modifier = Modifier.size(22.dp))
    }
}

data class CsNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val CsBottomNavItems: List<CsNavItem> = listOf(
    CsNavItem("dashboard", "Beranda", CsIcons.Home),
    CsNavItem("storage", "Storage", CsIcons.BarChart),
    CsNavItem("clean", "Bersih", CsIcons.Sparkles),
    CsNavItem("settings", "Setelan", CsIcons.Settings),
)

@Composable
fun CsBottomNav(
    items: List<CsNavItem>,
    selectedRoute: String,
    onSelect: (CsNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MaterialTheme.colorsExt
    Column(modifier = modifier.fillMaxWidth().background(ext.neuBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.space8, vertical = Dimens.space8),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEach { item ->
                val selected = item.route == selectedRoute
                val tint = if (selected) CsPalette.BrandGreen else ext.textFaint
                Column(
                    modifier = Modifier
                        .let {
                            if (selected) it.neuRaised(
                                backgroundColor = ext.neuBg,
                                shadowDark = ext.neuShadowDark,
                                shadowLight = ext.neuShadowLight,
                                cornerRadius = Dimens.radiusChip,
                                elevation = 3.dp,
                            ).clip(RoundedCornerShape(Dimens.radiusChip)) else it
                        }
                        .clickable { onSelect(item) }
                        .padding(horizontal = Dimens.space14, vertical = Dimens.space6),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(item.icon, contentDescription = item.label, tint = tint, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.size(Dimens.space4))
                    Text(item.label, style = MaterialTheme.typography.labelSmall, color = tint)
                }
            }
        }
    }
}
