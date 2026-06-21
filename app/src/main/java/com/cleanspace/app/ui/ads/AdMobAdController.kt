package com.cleanspace.app.ui.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AdMob-backed [AdController]. Keeps one preloaded interstitial and one rewarded
 * ad in memory and reloads after each show. Frequency cap is enforced in
 * [maybeShowInterstitial]. Banners are intentionally never used.
 */
@Singleton
class AdMobAdController @Inject constructor(
    @ApplicationContext private val context: Context,
) : AdController {

    private val initialized = AtomicBoolean(false)

    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var lastInterstitialAtMs = 0L
    private var loadingInterstitial = false
    private var loadingRewarded = false

    override fun initialize(activity: Activity) {
        if (initialized.getAndSet(true)) return
        MobileAds.initialize(context) {
            preloadInterstitial(activity)
            preloadRewarded(activity)
        }
    }

    override fun preloadInterstitial(activity: Activity) {
        if (interstitial != null || loadingInterstitial) return
        loadingInterstitial = true
        InterstitialAd.load(
            context,
            AdIds.interstitial,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitial = ad
                    loadingInterstitial = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitial = null
                    loadingInterstitial = false
                    Log.w(TAG, "interstitial load failed: ${error.message}")
                }
            },
        )
    }

    override fun maybeShowInterstitial(activity: Activity, onDone: () -> Unit) {
        val now = System.currentTimeMillis()
        val ad = interstitial
        if (ad == null || now - lastInterstitialAtMs < AdPolicy.INTERSTITIAL_MIN_GAP_MS) {
            onDone()
            preloadInterstitial(activity)
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitial = null
                lastInterstitialAtMs = System.currentTimeMillis()
                preloadInterstitial(activity)
                onDone()
            }

            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                interstitial = null
                preloadInterstitial(activity)
                onDone()
            }
        }
        ad.show(activity)
    }

    override fun preloadRewarded(activity: Activity) {
        if (rewarded != null || loadingRewarded) return
        loadingRewarded = true
        RewardedAd.load(
            context,
            AdIds.rewarded,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewarded = ad
                    loadingRewarded = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewarded = null
                    loadingRewarded = false
                    Log.w(TAG, "rewarded load failed: ${error.message}")
                }
            },
        )
    }

    override fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) {
        val ad = rewarded
        if (ad == null) {
            // No ad ready: don't block the user — free unlock + try to reload.
            preloadRewarded(activity)
            onReward()
            onClosed()
            return
        }
        var earned = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewarded = null
                preloadRewarded(activity)
                if (earned) onReward()
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                rewarded = null
                preloadRewarded(activity)
                onReward()
                onClosed()
            }
        }
        ad.show(activity, OnUserEarnedRewardListener { earned = true })
    }

    private companion object {
        const val TAG = "AdMobAdController"
    }
}
