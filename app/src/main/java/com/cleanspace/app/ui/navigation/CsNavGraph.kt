package com.cleanspace.app.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cleanspace.app.ui.components.CsBottomNav
import com.cleanspace.app.ui.components.CsBottomNavItems
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.screens.apps.AppManagerScreen
import com.cleanspace.app.ui.screens.apps.sampleApps
import com.cleanspace.app.ui.screens.clean.CleanHubScreen
import com.cleanspace.app.ui.screens.clean.cleanTools
import com.cleanspace.app.ui.screens.dashboard.DashboardScreen
import com.cleanspace.app.ui.screens.dashboard.sampleDashboardState
import com.cleanspace.app.ui.screens.done.DoneScreen
import com.cleanspace.app.ui.screens.done.sampleNextSteps
import com.cleanspace.app.ui.screens.duplicates.DuplicateFinderScreen
import com.cleanspace.app.ui.screens.duplicates.sampleDuplicateGroups
import com.cleanspace.app.ui.screens.hidden.HiddenFoldersScreen
import com.cleanspace.app.ui.screens.hidden.sampleHiddenFolders
import com.cleanspace.app.ui.screens.largest.LargestFilesScreen
import com.cleanspace.app.ui.screens.largest.sampleLargeFiles
import com.cleanspace.app.ui.screens.scanning.ScanningScreen
import com.cleanspace.app.ui.screens.scanning.sampleScanningState
import com.cleanspace.app.ui.screens.storage.StorageOverviewScreen
import com.cleanspace.app.ui.screens.storage.sampleStorageOverview
import com.cleanspace.app.ui.screens.whatsapp.WhatsAppCleanerScreen
import com.cleanspace.app.ui.screens.whatsapp.sampleWaMedia

object Routes {
    const val DASHBOARD = "dashboard"
    const val STORAGE = "storage"
    const val CLEAN = "clean"
    const val SETTINGS = "settings"
    const val DUPLICATES = "duplicates"
    const val WHATSAPP = "whatsapp"
    const val LARGEST = "largest"
    const val HIDDEN = "hidden"
    const val APPS = "apps"
    const val SCANNING = "scanning"
    const val DONE = "done"
}

private val TopLevelRoutes = CsBottomNavItems.map { it.route }.toSet()

/** Opens the official per-app storage settings page (policy-compliant cache clearing). */
fun openAppStorageSettings(context: Context, pkg: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$pkg"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

/** Launches Android's official uninstall dialog. */
fun uninstallApp(context: Context, pkg: String) {
    val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$pkg"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

@Composable
fun CleanSpaceApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.DASHBOARD

    Scaffold(
        bottomBar = {
            if (currentRoute in TopLevelRoutes) {
                CsBottomNav(
                    items = CsBottomNavItems,
                    selectedRoute = currentRoute,
                    onSelect = { item ->
                        navController.navigate(item.route) {
                            popUpTo(Routes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            csGraph(
                navigate = { route -> navController.navigate(route) },
                back = { navController.popBackStack() },
                openAppSettings = { pkg -> openAppStorageSettings(context, pkg) },
                uninstall = { pkg -> uninstallApp(context, pkg) },
            )
        }
    }
}

private fun NavGraphBuilder.csGraph(
    navigate: (String) -> Unit,
    back: () -> Unit,
    openAppSettings: (String) -> Unit,
    uninstall: (String) -> Unit,
) {
    composable(Routes.DASHBOARD) {
        DashboardScreen(
            state = sampleDashboardState(),
            onRecommendationClick = { rec ->
                when (rec.id) {
                    "dup" -> navigate(Routes.DUPLICATES)
                    "wa" -> navigate(Routes.WHATSAPP)
                    "large" -> navigate(Routes.LARGEST)
                    else -> navigate(Routes.CLEAN)
                }
            },
        )
    }
    composable(Routes.STORAGE) {
        StorageOverviewScreen(
            state = sampleStorageOverview(),
            onBack = back,
            onCategoryClick = { navigate(Routes.LARGEST) },
        )
    }
    composable(Routes.CLEAN) {
        CleanHubScreen(
            tools = cleanTools(),
            onToolClick = { tool -> navigate(tool.route) },
            onDeepClean = { navigate(Routes.SCANNING) },
        )
    }
    composable(Routes.SETTINGS) { SettingsPlaceholder() }
    composable(Routes.DUPLICATES) {
        DuplicateFinderScreen(
            groups = sampleDuplicateGroups(),
            reclaimableLabel = "3.2 GB",
            onBack = back,
            onCleanAll = { navigate(Routes.DONE) },
        )
    }
    composable(Routes.WHATSAPP) {
        WhatsAppCleanerScreen(items = sampleWaMedia(), onBack = back, onClean = { navigate(Routes.DONE) })
    }
    composable(Routes.LARGEST) {
        LargestFilesScreen(files = sampleLargeFiles(), onBack = back, onDelete = { navigate(Routes.DONE) })
    }
    composable(Routes.HIDDEN) {
        HiddenFoldersScreen(folders = sampleHiddenFolders(), onBack = back, onDelete = { navigate(Routes.DONE) })
    }
    composable(Routes.APPS) {
        AppManagerScreen(
            apps = sampleApps(),
            onBack = back,
            onOpenAppSettings = openAppSettings,
            onUninstall = uninstall,
        )
    }
    composable(Routes.SCANNING) {
        ScanningScreen(state = sampleScanningState())
    }
    composable(Routes.DONE) {
        DoneScreen(
            freedLabel = "12.8 GB",
            filesRemovedLabel = "512 file",
            nextSteps = sampleNextSteps(),
            onDone = back,
            onNextStep = { step -> if (step.id == "apps") navigate(Routes.APPS) else navigate(Routes.HIDDEN) },
        )
    }
}

@Composable
private fun SettingsPlaceholder() {
    androidx.compose.foundation.layout.Column(Modifier.fillMaxSize()) {
        CsTopBar(title = "Setelan")
        Text(
            "Setelan — segera hadir",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp),
        )
    }
}
