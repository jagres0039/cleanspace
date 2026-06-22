package com.cleanspace.app.ui.navigation

import android.app.Activity
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cleanspace.app.core.permissions.CsPermissions
import com.cleanspace.app.core.util.formatBytes
import com.cleanspace.app.ui.ads.LocalAdController
import com.cleanspace.app.ui.components.CsBottomNav
import com.cleanspace.app.ui.components.CsBottomNavItems
import com.cleanspace.app.ui.components.CsTopBar
import com.cleanspace.app.ui.screens.apps.AppManagerRoute
import com.cleanspace.app.ui.screens.clean.CleanHubScreen
import com.cleanspace.app.ui.screens.clean.cleanTools
import com.cleanspace.app.ui.screens.dashboard.DashboardRoute
import com.cleanspace.app.ui.screens.done.DoneScreen
import com.cleanspace.app.ui.screens.done.sampleNextSteps
import com.cleanspace.app.ui.screens.duplicates.DuplicateFinderRoute
import com.cleanspace.app.ui.screens.hidden.HiddenFoldersRoute
import com.cleanspace.app.ui.screens.largest.LargestFilesRoute
import com.cleanspace.app.ui.screens.permission.PermissionRoute
import com.cleanspace.app.ui.screens.scanning.ScanningRoute
import com.cleanspace.app.ui.screens.storage.StorageOverviewRoute
import com.cleanspace.app.ui.screens.whatsapp.WhatsAppRoute

object Routes {
    const val PERMISSION = "permission"
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

    fun done(freedBytes: Long, files: Int) = "$DONE?freedBytes=$freedBytes&files=$files"
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
    val adController = LocalAdController.current
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.DASHBOARD

    val startDestination = remember {
        if (CsPermissions.hasMinimum(context)) Routes.DASHBOARD else Routes.PERMISSION
    }

    // Capped interstitial: only shows when the policy gap has elapsed, then runs [done].
    val showInterstitialThen: (() -> Unit) -> Unit = { done ->
        val activity = context as? Activity
        if (activity != null) adController.maybeShowInterstitial(activity, done) else done()
    }

    // Deep Clean finished — go to Done with real numbers and drop Scanning from the back stack.
    val onDeepCleanFinished: (Long, Int) -> Unit = { freedBytes, files ->
        navController.navigate(Routes.done(freedBytes, files)) {
            popUpTo(Routes.SCANNING) { inclusive = true }
        }
    }

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
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            composable(Routes.PERMISSION) {
                PermissionRoute(
                    onContinue = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.PERMISSION) { inclusive = true }
                        }
                    },
                )
            }
            csGraph(
                navigate = { route -> navController.navigate(route) },
                back = { navController.popBackStack() },
                openAppSettings = { pkg -> openAppStorageSettings(context, pkg) },
                uninstall = { pkg -> uninstallApp(context, pkg) },
                showInterstitialThen = showInterstitialThen,
                onDeepCleanFinished = onDeepCleanFinished,
            )
        }
    }
}

private fun NavGraphBuilder.csGraph(
    navigate: (String) -> Unit,
    back: () -> Unit,
    openAppSettings: (String) -> Unit,
    uninstall: (String) -> Unit,
    showInterstitialThen: (() -> Unit) -> Unit,
    onDeepCleanFinished: (Long, Int) -> Unit,
) {
    composable(Routes.DASHBOARD) {
        DashboardRoute(
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
        StorageOverviewRoute(
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
        DuplicateFinderRoute(onBack = back)
    }
    composable(Routes.WHATSAPP) {
        WhatsAppRoute(onBack = back)
    }
    composable(Routes.LARGEST) {
        LargestFilesRoute(onBack = back)
    }
    composable(Routes.HIDDEN) {
        HiddenFoldersRoute(onBack = back)
    }
    composable(Routes.APPS) {
        AppManagerRoute(
            onBack = back,
            onOpenAppSettings = openAppSettings,
            onUninstall = uninstall,
        )
    }
    composable(Routes.SCANNING) {
        ScanningRoute(
            onFinished = onDeepCleanFinished,
            onBack = back,
        )
    }
    composable(
        route = "${Routes.DONE}?freedBytes={freedBytes}&files={files}",
        arguments = listOf(
            navArgument("freedBytes") { type = NavType.LongType; defaultValue = 0L },
            navArgument("files") { type = NavType.IntType; defaultValue = 0 },
        ),
    ) { entry ->
        val freedBytes = entry.arguments?.getLong("freedBytes") ?: 0L
        val files = entry.arguments?.getInt("files") ?: 0
        DoneScreen(
            freedLabel = formatBytes(freedBytes),
            filesRemovedLabel = "$files file",
            nextSteps = sampleNextSteps(),
            onDone = { showInterstitialThen(back) },
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
