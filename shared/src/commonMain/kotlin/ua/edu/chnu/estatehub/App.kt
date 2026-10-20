package ua.edu.chnu.estatehub

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ua.edu.chnu.estatehub.data.AppPreferences
import ua.edu.chnu.estatehub.ui.home.HomeScreen
import ua.edu.chnu.estatehub.ui.onboarding.OnboardingScreen
import ua.edu.chnu.estatehub.ui.theme.EstateHubTheme

@Composable
fun App() {
    val preferences = remember { AppPreferences() }

    var showOnboarding by remember { mutableStateOf(!preferences.onboardingCompleted) }
    var darkOverride by remember { mutableStateOf(preferences.darkThemeOverride) }
    val darkTheme = darkOverride ?: isSystemInDarkTheme()

    EstateHubTheme(darkTheme = darkTheme) {
        if (showOnboarding) {
            OnboardingScreen(
                onFinished = {
                    preferences.onboardingCompleted = true
                    showOnboarding = false
                },
            )
        } else {
            HomeScreen(
                darkTheme = darkTheme,
                onDarkThemeChange = { enabled ->
                    preferences.darkThemeOverride = enabled
                    darkOverride = enabled
                },
                onShowOnboardingAgain = {
                    preferences.onboardingCompleted = false
                    showOnboarding = true
                },
            )
        }
    }
}