package com.cleanspace.app.ui.ads

import com.cleanspace.app.BuildConfig

/**
 * Single source of truth for AdMob ad-unit ids.
 *
 * Debug builds always use Google's OFFICIAL test ids (safe, never bannable).
 * Release builds use the real ids below once [USE_TEST_ADS] is false.
 *
 * Publisher: ca-app-pub-1372627599973899
 * App ID (in AndroidManifest.xml): ca-app-pub-1372627599973899~8967386576
 */
object AdIds {

    // false = release uses the real PROD_* ids below. Debug still uses test ids.
    private const val USE_TEST_ADS = false

    // --- Google official TEST ids (used in debug builds) ---
    private const val TEST_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
    private const val TEST_REWARDED = "ca-app-pub-3940256099942544/5224354917"
    private const val TEST_NATIVE = "ca-app-pub-3940256099942544/2247696110"

    // --- Real AdMob unit ids (production) ---
    private const val PROD_INTERSTITIAL = "ca-app-pub-1372627599973899/1779006169" // "Space"
    private const val PROD_REWARDED = "ca-app-pub-1372627599973899/3092087839"     // "Clean"
    private const val PROD_NATIVE = "ca-app-pub-1372627599973899/9465924497"       // "clean"

    private val live: Boolean get() = !USE_TEST_ADS && !BuildConfig.DEBUG

    val interstitial: String get() = if (live) PROD_INTERSTITIAL else TEST_INTERSTITIAL
    val rewarded: String get() = if (live) PROD_REWARDED else TEST_REWARDED
    val native: String get() = if (live) PROD_NATIVE else TEST_NATIVE
}
