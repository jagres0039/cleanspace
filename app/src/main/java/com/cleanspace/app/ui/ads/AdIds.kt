package com.cleanspace.app.ui.ads

import com.cleanspace.app.BuildConfig

/**
 * Single source of truth for AdMob ad-unit ids.
 *
 * While [USE_TEST_ADS] is true (or in debug builds) we use Google's OFFICIAL
 * test ids — safe to ship during development and impossible to get banned for.
 *
 * Before production: paste the real ids from the AdMob console into the PROD_*
 * constants and set [USE_TEST_ADS] = false. Also swap the test App ID in
 * AndroidManifest.xml for the real one.
 */
object AdIds {

    // Flip to false ONLY after the PROD_* ids below are filled in.
    private const val USE_TEST_ADS = true

    // --- Google official TEST ids ---
    private const val TEST_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
    private const val TEST_REWARDED = "ca-app-pub-3940256099942544/5224354917"
    private const val TEST_NATIVE = "ca-app-pub-3940256099942544/2247696110"

    // --- TODO: real AdMob unit ids (production) ---
    private const val PROD_INTERSTITIAL = "ca-app-pub-0000000000000000/0000000000"
    private const val PROD_REWARDED = "ca-app-pub-0000000000000000/0000000000"
    private const val PROD_NATIVE = "ca-app-pub-0000000000000000/0000000000"

    private val live: Boolean get() = !USE_TEST_ADS && !BuildConfig.DEBUG

    val interstitial: String get() = if (live) PROD_INTERSTITIAL else TEST_INTERSTITIAL
    val rewarded: String get() = if (live) PROD_REWARDED else TEST_REWARDED
    val native: String get() = if (live) PROD_NATIVE else TEST_NATIVE
}
