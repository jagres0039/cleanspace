package com.cleanspace.app.ui.screens.clean

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.ui.icons.CsIcons
import com.cleanspace.app.ui.theme.CsPalette

@Composable
fun CleanHubRoute(
    onToolClick: (CleanTool) -> Unit,
    onDeepClean: () -> Unit,
    vm: CleanHubViewModel = hiltViewModel(),
) {
    val sizes by vm.state.collectAsState()

    // Re-scan whenever the tab becomes visible again so the per-tool sizes drop
    // right after the user cleans a category.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.load()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Show "…" until the first scan finishes, then the real reclaimable size.
    fun label(bytes: Long): String = if (!sizes.ready) "…" else formatBytes(bytes)

    val tools = listOf(
        CleanTool("duplicates", CsIcons.Copy, CsPalette.Chart8, "File duplikat", "Salinan kembar, auto-keep terbaik", label(sizes.duplicateBytes)),
        CleanTool("whatsapp", CsIcons.MessageCircle, CsPalette.Chart3, "Media WhatsApp", "Foto, video, voice note", label(sizes.whatsAppBytes)),
        CleanTool("largest", CsIcons.Box, CsPalette.Chart1, "File besar (>100 MB)", "File & data paling makan ruang", label(sizes.largeBytes)),
        CleanTool("hidden", CsIcons.Image, CsPalette.Chart6, "Folder tersembunyi", ".thumbnails, sisa app, file temp", label(sizes.hiddenBytes)),
        CleanTool("apps", CsIcons.Smartphone, CsPalette.Chart5, "Kelola aplikasi", "Cache & app jarang dipakai", label(sizes.appsCacheBytes)),
    )

    CleanHubScreen(
        tools = tools,
        onToolClick = onToolClick,
        onDeepClean = onDeepClean,
    )
}
