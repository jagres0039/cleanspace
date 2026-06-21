package com.cleanspace.app.ui.ads

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.cleanspace.app.ui.components.CsCard
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * A single, self-contained native ad rendered as a CleanSpace card. It loads its
 * own ad on first composition and hides itself entirely until/unless an ad is
 * available (so failed loads leave no empty box). Clearly labelled "Bersponsor".
 *
 * Drop this into a list every [AdPolicy.NATIVE_EVERY] items.
 */
@Composable
fun NativeAdSlot(modifier: Modifier = Modifier) {
    // Never touch the SDK in @Preview / inspection.
    if (LocalInspectionMode.current) return

    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(Unit) {
        val loader = AdLoader.Builder(context, AdIds.native)
            .forNativeAd { ad ->
                nativeAd?.destroy()
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Leave the slot hidden on failure.
                }
            })
            .build()
        loader.loadAd(AdRequest.Builder().build())
        onDispose { nativeAd?.destroy() }
    }

    val ad = nativeAd ?: return
    CsCard {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx -> buildNativeAdView(ctx) },
            update = { view -> bindNativeAd(view, ad) },
        )
    }
}

private fun buildNativeAdView(ctx: Context): NativeAdView {
    val density = ctx.resources.displayMetrics.density
    fun dp(v: Int) = (v * density).toInt()

    val adView = NativeAdView(ctx).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }
    val container = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }
    val sponsor = TextView(ctx).apply {
        text = "Bersponsor"
        textSize = 11f
        alpha = 0.6f
    }
    val headline = TextView(ctx).apply {
        textSize = 15f
        setPadding(0, dp(2), 0, 0)
    }
    val body = TextView(ctx).apply {
        textSize = 13f
        alpha = 0.8f
        setPadding(0, dp(2), 0, dp(6))
    }
    val cta = Button(ctx)

    container.addView(sponsor)
    container.addView(headline)
    container.addView(body)
    container.addView(cta)
    adView.addView(container)

    adView.headlineView = headline
    adView.bodyView = body
    adView.callToActionView = cta
    return adView
}

private fun bindNativeAd(adView: NativeAdView, ad: NativeAd) {
    (adView.headlineView as? TextView)?.text = ad.headline
    (adView.bodyView as? TextView)?.apply {
        text = ad.body
        visibility = if (ad.body.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
    (adView.callToActionView as? Button)?.apply {
        text = ad.callToAction
        visibility = if (ad.callToAction.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
    adView.setNativeAd(ad)
}
