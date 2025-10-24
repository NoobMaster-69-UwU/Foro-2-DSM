package sv.edu.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // <-- Add this import
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sv.edu.controlgastos.ui.theme.ControlgastosTheme

class MainActivity : ComponentActivity() {
    // Create an instance of the ThemeViewModel
    private val themeViewModel by viewModels<ThemeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Get the current dark mode state from the ViewModel
            val isDarkMode by themeViewModel.isDarkMode

            // This sets the initial theme only once when the app starts
            val systemInDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(Unit) {
                themeViewModel.setInitialTheme(systemInDarkTheme)
            }

            // Apply the theme based on the ViewModel's state
            ControlgastosTheme(darkTheme = isDarkMode) {
                AppNavigation(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("main") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("main") {
            // Pass the ViewModel to the GastosScreen
            GastosScreen(themeViewModel = themeViewModel)
        }
    }
}
