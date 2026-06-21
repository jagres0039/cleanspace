package com.cleanspace.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cleanspace.app.presentation.navigation.CleanSpaceNavHost
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CleanSpaceTheme {
                CleanSpaceNavHost()
            }
        }
    }
}
