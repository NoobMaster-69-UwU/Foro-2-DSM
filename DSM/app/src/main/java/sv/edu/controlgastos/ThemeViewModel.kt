package sv.edu.controlgastos

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    // State to hold whether dark mode is enabled or not
    val isDarkMode = mutableStateOf(false) // Default to light mode

    // Function to toggle the theme
    fun toggleTheme() {
        isDarkMode.value = !isDarkMode.value
    }

    // Function to set the initial theme based on the system setting
    fun setInitialTheme(isSystemInDarkTheme: Boolean) {
        isDarkMode.value = isSystemInDarkTheme
    }
}
