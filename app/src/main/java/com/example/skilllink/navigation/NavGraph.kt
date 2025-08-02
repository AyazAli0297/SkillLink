package com.example.skilllink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skilllink.ui.screens.CustomerDashboardScreen
import com.example.skilllink.ui.screens.SignInScreen
import com.example.skilllink.ui.screens.SignUpScreen
import com.example.skilllink.ui.screens.SkilledDashboardScreen
import com.example.skilllink.ui.screens.SkilledProfileScreen
import com.example.skilllink.ui.screens.WelcomeScreen

sealed class Screen(val route: String) {
    object Welcome           : Screen("welcome")
    object SignIn            : Screen("signin")
    object SignUp            : Screen("signup")
    object CustomerDashboard : Screen("customer_dashboard")
    object SkilledProfile    : Screen("skilled_profile")
    object SkilledDashboard  : Screen("skilled_dashboard")
    object Payment           : Screen("payment")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignIn =    { navController.navigate(Screen.SignIn.route) },
                onSignUp =    { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                navController = navController,
                onSignUp       = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword = { 
                    // This will be handled in the SignInScreen component
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                onSignUp      = { /* Navigation is handled in SignUpScreen.kt based on role */ },
                onSignIn      = { navController.navigate(Screen.SignIn.route) }
            )
        }

        composable(Screen.CustomerDashboard.route) {
            CustomerDashboardScreen(navController)
        }

        composable(Screen.SkilledProfile.route) {
            SkilledProfileScreen(
                navController = navController,
                onProfileComplete = {
                    navController.navigate(Screen.SkilledDashboard.route) {
                        popUpTo(Screen.SkilledProfile.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.SkilledDashboard.route) {
            SkilledDashboardScreen(navController)
        }
    }
}
