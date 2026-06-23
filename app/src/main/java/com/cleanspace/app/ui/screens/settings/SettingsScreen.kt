package com.cleanspace.app.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cleanspace.app.BuildConfig
import com.cleanspace.app.core.work.CleanWork
import com.cleanspace.app.ui.components.BrandGradient
import com.cleanspace.app.ui.components.CsCallout
import com.cleanspace.app.ui.components.CsCalloutTone
import com.cleanspace.app.ui.components.CsCard
import com.cleanspace.app.ui.components.CsListRow
import com.cleanspace.app.ui.components.CsSectionLabel
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette
import com.cleanspace.app.ui.theme.Dimens
import com.cleanspace.app.ui.theme.colorsExt

private const val PRIVACY_URL = "https://jagres0039.github.io/cleanspace/privacy-policy.html"
private const val PREFS = "cleanspace_settings"

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val ext = MaterialTheme.colorsExt
    val prefs = remember { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    var autoScan by remember { mutableStateOf(prefs.getBoolean("auto_scan", true)) }
    var notify by remember { mutableStateOf(prefs.getBoolean("notify", true)) }

    fun setAutoScan(value: Boolean) {
        autoScan = value
        prefs.edit().putBoolean("auto_scan", value).apply()
        if (value) CleanWork.schedulePeriodicScan(context) else CleanWork.cancelPeriodicScan(context)
    }
    fun setNotify(value: Boolean) {
        notify = value
        prefs.edit().putBoolean("notify", value).apply()
    }

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = CsPalette.BrandGreen,
    )

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CsTopBar(title = "Setelan")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.screenPaddingH),
            verticalArrangement = Arrangement.spacedBy(Dimens.space16),
        ) {
            Spacer(Modifier.size(Dimens.space8))

            // Brand header
            CsCard {
                Row(
                    Modifier.fillMaxWidth().padding(Dimens.space14),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BrandGradient),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(CsIcons.Sparkles, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.size(Dimens.space12))
                    Column(Modifier.weight(1f)) {
                        Text("CleanSpace", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        Text("by JAGRESTECH · v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = ext.textSoft)
                    }
                }
            }

            // Pemindaian
            CsSectionLabel("Pemindaian")
            CsCard {
                Column {
                    CsListRow(
                        icon = CsIcons.Refresh,
                        iconTint = CsPalette.Chart3,
                        title = "Pindai otomatis",
                        subtitle = "Cek sampah tiap hari di background",
                        onClick = { setAutoScan(!autoScan) },
                        trailing = { Switch(checked = autoScan, onCheckedChange = { setAutoScan(it) }, colors = switchColors) },
                    )
                    RowDivider()
                    CsListRow(
                        icon = CsIcons.Info,
                        iconTint = CsPalette.Chart1,
                        title = "Notifikasi pengingat",
                        subtitle = "Ingatkan saat sampah menumpuk",
                        onClick = { setNotify(!notify) },
                        trailing = { Switch(checked = notify, onCheckedChange = { setNotify(it) }, colors = switchColors) },
                    )
                }
            }

            // Privasi & izin
            CsSectionLabel("Privasi & izin")
            CsCard {
                Column {
                    CsListRow(
                        icon = CsIcons.ShieldCheck,
                        iconTint = ext.success,
                        title = "Kelola izin aplikasi",
                        subtitle = "Akses media, semua file & penggunaan",
                        onClick = { openAppInfo(context) },
                        trailing = { Chevron() },
                    )
                    RowDivider()
                    CsListRow(
                        icon = CsIcons.FileText,
                        iconTint = CsPalette.Chart2,
                        title = "Kebijakan privasi",
                        subtitle = "Buka di browser",
                        onClick = { openUrl(context, PRIVACY_URL) },
                        trailing = { Chevron() },
                    )
                }
            }

            // Iklan
            CsSectionLabel("Iklan")
            CsCallout(
                text = "Iklan ringan (tanpa banner) bikin CleanSpace tetap gratis. Personalisasi iklan mengikuti persetujuan privasi kamu — bisa diubah lewat \"Kelola izin\".",
                tone = CsCalloutTone.Info,
                icon = CsIcons.Sparkles,
            )

            // Tentang
            CsSectionLabel("Tentang")
            CsCard {
                Column {
                    CsListRow(
                        icon = CsIcons.Sparkles,
                        iconTint = CsPalette.Chart6,
                        title = "Beri rating di Play Store",
                        onClick = { rateApp(context) },
                        trailing = { Chevron() },
                    )
                    RowDivider()
                    CsListRow(
                        icon = CsIcons.MessageCircle,
                        iconTint = CsPalette.Chart3,
                        title = "Bagikan CleanSpace",
                        onClick = { shareApp(context) },
                        trailing = { Chevron() },
                    )
                    RowDivider()
                    CsListRow(
                        icon = CsIcons.Info,
                        iconTint = CsPalette.Chart1,
                        title = "Versi",
                        subtitle = "CleanSpace untuk Android",
                        trailing = { Text("v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = ext.textSoft) },
                    )
                }
            }

            Spacer(Modifier.size(Dimens.space24))
        }
    }
}

@Composable
private fun Chevron() {
    Icon(
        CsIcons.ChevronRight,
        contentDescription = null,
        tint = MaterialTheme.colorsExt.textFaint,
        modifier = Modifier.size(18.dp),
    )
}

@Composable
private fun RowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = Dimens.space14)
            .height(Dimens.borderHairline)
            .background(MaterialTheme.colorsExt.border),
    )
}

private fun openUrl(context: Context, url: String) {
    runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}

private fun openAppInfo(context: Context) {
    runCatching {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}

private fun rateApp(context: Context) {
    val pkg = context.packageName
    val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (market.resolveActivity(context.packageManager) != null) {
        runCatching { context.startActivity(market) }
    } else {
        openUrl(context, "https://play.google.com/store/apps/details?id=$pkg")
    }
}

private fun shareApp(context: Context) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Coba CleanSpace buat bersihin sampah HP: https://play.google.com/store/apps/details?id=${context.packageName}",
        )
    }
    runCatching {
        context.startActivity(Intent.createChooser(send, "Bagikan").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
