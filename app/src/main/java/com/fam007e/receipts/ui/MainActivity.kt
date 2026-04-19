package com.fam007e.receipts.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text
import com.fam007e.receipts.ui.screens.home.HomeScreen
import com.fam007e.receipts.ui.screens.onboarding.OnboardingScreen
import com.fam007e.receipts.ui.theme.ReceiptsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // In a real app, I'd get the mode from a ViewModel/DataStore
            // For now, default to "receipts"
            val mode = "receipts" 
            
            ReceiptsTheme(mode = mode) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Onboarding.route
                ) {
                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(onComplete = { _ ->
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        })
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(onNavigateToCapture = {
                            navController.navigate(Screen.Capture.createRoute(null))
                        })
                    }
                    // ... other routes
                }
            }
        }
    }
}
