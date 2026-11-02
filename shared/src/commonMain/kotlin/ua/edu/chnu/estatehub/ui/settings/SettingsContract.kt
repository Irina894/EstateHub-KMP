package ua.edu.chnu.estatehub.ui.settings

import ua.edu.chnu.estatehub.domain.model.AppSettings
import ua.edu.chnu.estatehub.domain.model.ThemeMode
import ua.edu.chnu.estatehub.locale.AppLanguage
import ua.edu.chnu.estatehub.navigation.Route

data class SettingsState(
    val settings: AppSettings,
    val username: String? = null,
)

sealed interface SettingsIntent {
    data class ThemeSelected(val mode: ThemeMode) : SettingsIntent
    data class LanguageSelected(val language: AppLanguage) : SettingsIntent
    data class NotificationsToggled(val enabled: Boolean) : SettingsIntent
    data object ShowOnboardingAgain : SettingsIntent
    data object SignOutClicked : SettingsIntent
}

sealed interface SettingsEffect {
    data class Navigate(val route: Route) : SettingsEffect
}
