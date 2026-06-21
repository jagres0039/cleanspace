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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.ui.components.BrandGradient
import com.cleanspace.app.ui.components.CsButton
import com.cleanspace.app.ui.components.CsButtonStyle
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsText
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Stateful permission gate. Requests media at runtime, and routes the user to
 * the dedicated Settings pages for All-files & Usage access. Statuses refresh
 * automatically when the user returns from Settings (ON_RESUME).
 *
 * Media access is the minimum to continue; the other two are optional unlocks.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRoute(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mediaState = rememberMultiplePermissionsState(CsPermissions.mediaPermissions)

    var allFiles by remember { mutableStateOf(CsPermissions.hasAllFilesAccess()) }
    var usage by remember { mutableStateOf(CsPermissions.hasUsageAccess(context)) }
    val media = mediaState.allPermissionsGranted || CsPermissions.hasAllFilesAccess()

    // Refresh Settings-driven grants whenever we come back to the foreground.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                allFiles = CsPermissions.hasAllFilesAccess()
                usage = CsPermissions.hasUsageAccess(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.space24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(56.dp))
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BrandGradient),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CsIcons.ShieldCheck, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(Dimens.space16))
        Text("Beri akses ke CleanSpace", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(Dimens.space4))
        Text(
            "Semua scan jalan lokal di HP kamu. Nggak ada file yang diunggah.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorsExt.textSoft,
        )
        Spacer(Modifier.height(Dimens.space24))

        PermissionStep(
            icon = CsIcons.Image,
            title = "Akses media",
            body = "Pindai foto, video, dan audio buat cari yang bisa dibersihkan.",
            granted = media,
            required = true,
            onAction = { mediaState.launchMultiplePermissionRequest() },
        )
        PermissionStep(
            icon = CsIcons.Box,
            title = "Akses semua file",
            body = "Buat folder tersembunyi & file besar di luar media (opsional).",
            granted = allFiles,
            required = false,
            onAction = { runCatching { context.startActivity(CsPermissions.allFilesAccessIntent(context)) } },
        )
        PermissionStep(
            icon = CsIcons.Smartphone,
            title = "Akses penggunaan",
            body = "Buat lihat ukuran cache & aplikasi yang jarang dipakai (opsional).",
            granted = usage,
            required = false,
            onAction = { runCatching { context.startActivity(CsPermissions.usageAccessIntent()) } },
        )

        Spacer(Modifier.height(Dimens.space24))
        CsButton(
            label = if (media) "Lanjut" else "Izinkan akses media",
            onClick = { if (media) onContinue() else mediaState.launchMultiplePermissionRequest() },
            leadingIcon = if (media) CsIcons.Check else CsIcons.ShieldCheck,
        )
        if (media) {
            Spacer(Modifier.height(Dimens.space10))
            Text(
                "Kamu bisa kasih izin tambahan kapan aja dari Setelan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorsExt.textFaint,
            )
        }
        Spacer(Modifier.height(Dimens.space24))
    }
}

@Composable
private fun PermissionStep(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    granted: Boolean,
    required: Boolean,
    onAction: () -> Unit,
) {
    val ext = MaterialTheme.colorsExt
    CsCard(modifier = Modifier.padding(bottom = Dimens.space8)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.space14),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.tileIcon)
                    .clip(RoundedCornerShape(Dimens.radiusChip))
                    .background((if (granted) ext.success else ext.info).copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (granted) CsIcons.CheckCircle else icon,
                    contentDescription = null,
                    tint = if (granted) ext.success else ext.info,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.size(Dimens.space12))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    if (!required) {
                        Spacer(Modifier.size(Dimens.space6))
                        Text("opsional", style = MaterialTheme.typography.labelSmall, color = ext.textFaint)
                    }
                }
                Text(body, style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
            }
            Spacer(Modifier.size(Dimens.space10))
            if (granted) {
                Icon(CsIcons.Check, contentDescription = "Granted", tint = ext.success, modifier = Modifier.size(20.dp))
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.radiusChip))
                        .background(ext.surfaceHover)
                        .clickableNoRipple(onAction)
                        .padding(horizontal = Dimens.space12, vertical = Dimens.space8),
                ) {
                    Text("Izinkan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(
        indication = null,
        interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
        onClick = onClick,
    ))
