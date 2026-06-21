package com.cleanspace.app.ui.ads

import android.app.Activity
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Wraps Google's User Messaging Platform (UMP) consent flow required for
 * GDPR/EEA users before serving personalized ads.
 *
 * Call [ensureConsent] once on app start. The callback reports whether ads can
 * be requested; we only initialize AdMob after that returns true.
 */
class ConsentManager(activity: Activity) {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    /** Whether the SDK currently has enough consent to request ads. */
    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    fun ensureConsent(activity: Activity, onResolved: (canRequestAds: Boolean) -> Unit) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // Info updated: show the consent form if the user must act.
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    onResolved(consentInformation.canRequestAds())
                }
            },
            {
                // Update failed (e.g. offline): proceed with whatever we have.
                onResolved(consentInformation.canRequestAds())
            },
        )
    }
}
