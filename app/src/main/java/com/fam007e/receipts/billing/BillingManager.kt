package com.fam007e.receipts.billing

import android.app.Activity
import android.content.Context
import com.fam007e.receipts.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FOSS No-Op Billing Manager.
 * In the F-Droid version, premium features are unlocked via the "Honor System"
 * or represent a "Donation" model.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val premiumRepository: com.fam007e.receipts.domain.repository.PremiumRepository
) {
    fun launchPremiumFlow(activity: Activity, sku: String) {
        val donationUrl = activity.getString(R.string.donation_url)
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(donationUrl)
        activity.startActivity(intent)
    }

    companion object {
        const val SKU_PREMIUM_MONTHLY = "donation_monthly"
        const val SKU_PREMIUM_ANNUAL = "donation_annual"
    }
}
