package com.cleanspace.app.ui.ads

import android.app.Activity
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Conservative, non-intrusive ad cadence. Tuned so the app never feels spammy
 * (key for passing Play review and keeping users): no banners, one native card
 * every few list items, and a long gap between interstitials.
 */
object AdPolicy {
    /** Insert a native ad after every Nth content item in a long list. */
    const val NATIVE_EVERY = 6

    /** Don't bother with native ads in short lists. */
    const val NATIVE_MIN_LIST_SIZE = 8

    /** Minimum time between two interstitials (3 minutes). */
    const val INTERSTITIAL_MIN_GAP_MS = 3 * 60 * 1000L
}

/** Abstraction over the ad SDK so UI/tests never touch AdMob directly. */
interface AdController {
    /** Initialize the underlying SDK. Safe to call multiple times. */
    fun initialize(activity: Activity)

    /** Preload an interstitial so it's ready when needed. */
    fun preloadInterstitial(activity: Activity)

    /**
     * Show an interstitial only if the frequency cap allows. [onDone] always
     * runs (shown or not) so the caller's flow never blocks.
     */
    fun maybeShowInterstitial(activity: Activity, onDone: () -> Unit)

    /** Preload the rewarded ad used to unlock Deep Clean. */
    fun preloadRewarded(activity: Activity)

    /**
     * Show the rewarded ad. [onReward] runs only if the reward is earned;
     * [onClosed] always runs after the flow ends. If no ad is available we treat
     * it as a free unlock (call both) rather than blocking the user.
     */
    fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit)
}

/** No-op controller for previews/tests and before the SDK is ready. */
object NoOpAdController : AdController {
    override fun initialize(activity: Activity) {}
    override fun preloadInterstitial(activity: Activity) {}
    override fun maybeShowInterstitial(activity: Activity, onDone: () -> Unit) = onDone()
    override fun preloadRewarded(activity: Activity) {}
    override fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) {
        onReward()
        onClosed()
    }
}

/** Lets composables reach the controller; defaults to no-op (e.g. in previews). */
val LocalAdController = staticCompositionLocalOf<AdController> { NoOpAdController }
