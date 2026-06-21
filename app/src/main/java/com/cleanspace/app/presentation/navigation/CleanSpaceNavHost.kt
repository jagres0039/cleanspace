package com.cleanspace.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cleanspace.app.presentation.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val DUPLICATES = "duplicates"
    const val LARGE_FILES = "large_files"
    const val WHATSAPP = "whatsapp"
}

@Composable
fun CleanSpaceNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen() }
        composable(Routes.DUPLICATES) { /* DuplicatesScreen() */ }
        composable(Routes.LARGE_FILES) { /* LargeFilesScreen() */ }
        composable(Routes.WHATSAPP) { /* WhatsAppScreen() */ }
    }
}
