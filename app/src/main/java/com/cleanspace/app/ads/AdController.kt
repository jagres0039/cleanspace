package com.cleanspace.app.ads

import android.app.Activity

/**
 * Ad placement strategy for CleanSpace (kept intentionally minimal & non-intrusive):
 *  - NATIVE: occasional native card inside long result lists (see NativeAdSlot).
 *  - REWARDED: optional, user-initiated, to unlock a one-shot "Deep Clean".
 *  - INTERSTITIAL: at most once, after a successful cleanup (frequency-capped).
 *
 * No banners. No ads on the permission/onboarding flow. No ads mid-action.
 *
 * This interface decouples the UI from any concrete SDK so we can wire AdMob
 * (primary) + AppLovin MAX (mediation) later without touching screens. The
 * default [NoOpAdController] lets the app build & run with zero ad SDK deps.
 */
interface AdController {
    /** Show an interstitial if one is loaded AND the frequency cap allows it. */
    fun maybeShowInterstitial(activity: Activity, onClosed: () -> Unit = {})

    /** Show a rewarded ad. [onReward] fires only if the user earned the reward. */
    fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit = {})

    /** Whether a native ad should be inserted at the given list index. */
    fun shouldShowNativeAt(index: Int): Boolean

    /** Preload ads (call early, e.g. on app start / after permission grant). */
    fun preload(activity: Activity) {}
}

/**
 * Default no-op controller. Rewarded grants the reward immediately (so debug
 * builds and ad-free states still work), interstitials are skipped, and no
 * native slots are injected.
 */
object NoOpAdController : AdController {
    override fun maybeShowInterstitial(activity: Activity, onClosed: () -> Unit) = onClosed()
    override fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) {
        onReward(); onClosed()
    }
    override fun shouldShowNativeAt(index: Int): Boolean = false
}

/** Tunable placement policy shared by real controllers. */
object AdPolicy {
    /** Insert a native ad every N rows in long lists (0 = never). */
    const val NATIVE_EVERY: Int = 6

    /** Minimum gap (ms) between two interstitials. */
    const val INTERSTITIAL_MIN_GAP_MS: Long = 3 * 60 * 1000L

    /** Don't insert native ads in lists shorter than this. */
    const val NATIVE_MIN_LIST_SIZE: Int = 8

    fun shouldShowNativeAt(index: Int, listSize: Int): Boolean =
        NATIVE_EVERY > 0 &&
            listSize >= NATIVE_MIN_LIST_SIZE &&
            index > 0 &&
            index % NATIVE_EVERY == 0
}
