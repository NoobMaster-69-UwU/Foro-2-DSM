package sv.edu.controlgastos.ui.theme

// ADDED: Imports for all the colors defined in your Color.kt file
import sv.edu.controlgastos.ui.theme.md_theme_dark_background
import sv.edu.controlgastos.ui.theme.md_theme_dark_onPrimary
import sv.edu.controlgastos.ui.theme.md_theme_dark_onSecondary
import sv.edu.controlgastos.ui.theme.md_theme_dark_onSurface
import sv.edu.controlgastos.ui.theme.md_theme_dark_primary
import sv.edu.controlgastos.ui.theme.md_theme_dark_primaryContainer
import sv.edu.controlgastos.ui.theme.md_theme_dark_secondary
import sv.edu.controlgastos.ui.theme.md_theme_dark_surface
import sv.edu.controlgastos.ui.theme.md_theme_dark_tertiary
import sv.edu.controlgastos.ui.theme.md_theme_light_background
import sv.edu.controlgastos.ui.theme.md_theme_light_onPrimary
import sv.edu.controlgastos.ui.theme.md_theme_light_onSecondary
import sv.edu.controlgastos.ui.theme.md_theme_light_onSurface
import sv.edu.controlgastos.ui.theme.md_theme_light_primary
import sv.edu.controlgastos.ui.theme.md_theme_light_primaryContainer
import sv.edu.controlgastos.ui.theme.md_theme_light_secondary
import sv.edu.controlgastos.ui.theme.md_theme_light_surface
import sv.edu.controlgastos.ui.theme.md_theme_light_tertiary

// FIXED: Import for Activity was missing/broken
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    tertiary = md_theme_dark_tertiary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    tertiary = md_theme_light_tertiary,
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
)

@Composable
fun ControlgastosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Changed to false to ensure our custom theme is always used
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // This should now resolve correctly
        content = content
    )
}
