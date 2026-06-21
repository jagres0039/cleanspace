package com.cleanspace.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.cleanspace.app.ui.ads.AdController
import com.cleanspace.app.ui.ads.ConsentManager
import com.cleanspace.app.ui.ads.LocalAdController
import com.cleanspace.app.ui.navigation.CleanSpaceApp
import com.cleanspace.app.ui.theme.CleanSpaceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var adController: AdController

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Gather GDPR/UMP consent first, then initialize AdMob only if allowed.
        ConsentManager(this).ensureConsent(this) { canRequestAds ->
            if (canRequestAds) adController.initialize(this)
        }

        setContent {
            CleanSpaceTheme {
                CompositionLocalProvider(LocalAdController provides adController) {
                    CleanSpaceApp()
                }
            }
        }
    }
}
