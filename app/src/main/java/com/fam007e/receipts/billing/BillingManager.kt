package com.fam007e.receipts.billing

import android.app.Activity
import android.content.Context
import com.fam007e.receipts.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FOSS No-Op Billing Manager.
 * All features are free. This provides a path for supporting development via external links.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun launchPremiumFlow(activity: Activity, _sku: String) {
        // Since we are FOSS, we redirect to a donation/support page
        val donationUrl = "https://github.com/fam007e/receipts#support" 
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(donationUrl)
        activity.startActivity(intent)
    }

    companion object {
        const val SKU_PREMIUM_MONTHLY = "support_monthly"
        const val SKU_PREMIUM_ANNUAL = "support_annual"
    }
}
