package com.fam007e.receipts.ui

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Feed : Screen("feed/{personId}") {
        fun createRoute(personId: Long) = "feed/$personId"
    }
    object Capture : Screen("capture?personId={personId}") {
        fun createRoute(personId: Long?) = "capture" + (personId?.let { "?personId=$it" } ?: "")
    }
    object Stats : Screen("stats")
    object Settings : Screen("settings")
    object Expose : Screen("expose/{personId}") {
        fun createRoute(personId: Long) = "expose/$personId"
    }
    object Coaching : Screen("coaching/{receiptId}") {
        fun createRoute(receiptId: Long) = "coaching/$receiptId"
    }
}
