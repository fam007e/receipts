package com.fam007e.receipts.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.compose.material3.Text
import com.fam007e.receipts.ui.screens.home.HomeScreen
import com.fam007e.receipts.ui.screens.onboarding.OnboardingScreen
import com.fam007e.receipts.ui.screens.settings.SettingsScreen
import com.fam007e.receipts.ui.screens.feed.FeedScreen
import com.fam007e.receipts.ui.screens.capture.CaptureScreen
import com.fam007e.receipts.ui.screens.stats.StatsScreen
import com.fam007e.receipts.ui.screens.premium.PremiumScreen
import com.fam007e.receipts.ui.theme.ReceiptsTheme
import com.fam007e.receipts.ui.theme.LittlesTheme
import com.fam007e.receipts.data.preferences.UserPreferences
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var databaseSeeder: com.fam007e.receipts.data.db.DatabaseSeeder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Seed achievements
        lifecycleScope.launch {
            databaseSeeder.seedAchievements()
        }

        setContent {
            val mode by userPreferences.appMode.collectAsState(initial = "receipts")
            val isOnboarded by userPreferences.isOnboarded.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            
            ReceiptsTheme(mode = mode) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = if (isOnboarded) Screen.Home.route else Screen.Onboarding.route
                ) {
                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(onComplete = { selectedMode ->
                            scope.launch {
                                userPreferences.setMode(selectedMode)
                                userPreferences.setOnboarded(true)
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        })
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToCapture = { personId ->
                                navController.navigate(Screen.Capture.createRoute(personId))
                            },
                            onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                            onNavigateToFeed = { personId -> navController.navigate(Screen.Feed.createRoute(personId)) },
                            onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                            onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                            onNavigateToPremium = { navController.navigate(Screen.Premium.route) }
                        )
                    }
                    composable(
                        route = Screen.Feed.route,
                        arguments = listOf(navArgument("personId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val personId = backStackEntry.arguments?.getString("personId")?.toLongOrNull() ?: -1L
                        FeedScreen(
                            personId = personId,
                            onNavigateToExpose = { navController.navigate(Screen.Expose.createRoute(personId)) },
                            onNavigateToCoaching = { receiptId -> navController.navigate(Screen.Coaching.createRoute(receiptId)) }
                        )
                    }
                    composable(
                        route = Screen.Capture.route,
                        arguments = listOf(navArgument("personId") { 
                            type = NavType.StringType
                            nullable = true 
                            defaultValue = null
                        })
                    ) { backStackEntry ->
                        val personId = backStackEntry.arguments?.getString("personId")?.toLongOrNull()
                        CaptureScreen(
                            personId = personId,
                            onCaptureComplete = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Stats.route) {
                        StatsScreen(onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Premium.route) {
                        PremiumScreen(
                            billingManager = hiltViewModel<com.fam007e.receipts.ui.screens.premium.PremiumViewModel>().billingManager,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Achievements.route) {
                        val achievementsViewModel: com.fam007e.receipts.ui.screens.achievements.AchievementsViewModel = hiltViewModel()
                        val achievements by achievementsViewModel.achievements.collectAsState()
                        com.fam007e.receipts.ui.screens.achievements.AchievementsScreen(
                            achievements = achievements,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Leaderboard.route) {
                        com.fam007e.receipts.ui.screens.leaderboard.LeaderboardScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.Expose.route,
                        arguments = listOf(navArgument("personId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val personId = backStackEntry.arguments?.getString("personId")?.toLongOrNull() ?: -1L
                        com.fam007e.receipts.ui.screens.expose.ExposeScreen(
                            personId = personId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.Coaching.route,
                        arguments = listOf(navArgument("receiptId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val receiptId = backStackEntry.arguments?.getString("receiptId")?.toLongOrNull() ?: -1L
                        com.fam007e.receipts.ui.screens.littles.LittlesCoachScreen(
                            receiptId = receiptId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
