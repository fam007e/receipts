package com.fam007e.receipts.ui.screens.premium

import androidx.lifecycle.ViewModel
import com.fam007e.receipts.billing.BillingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    val billingManager: BillingManager
) : ViewModel()
