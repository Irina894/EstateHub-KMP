package ua.edu.chnu.estatehub.ui.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.usecase.GetCurrentUserUseCase
import ua.edu.chnu.estatehub.domain.usecase.ObserveAppSettingsUseCase
import ua.edu.chnu.estatehub.domain.usecase.SaveOnboardingResultUseCase
import ua.edu.chnu.estatehub.domain.usecase.SignOutUseCase
import ua.edu.chnu.estatehub.domain.usecase.UpdateLanguageUseCase
import ua.edu.chnu.estatehub.domain.usecase.UpdateNotificationsUseCase
import ua.edu.chnu.estatehub.domain.usecase.UpdateThemeModeUseCase
import ua.edu.chnu.estatehub.navigation.Route

class SettingsViewModel(
    observeSettings: ObserveAppSettingsUseCase,
    private val updateTheme: UpdateThemeModeUseCase,
    private val updateLanguage: UpdateLanguageUseCase,
    private val updateNotifications: UpdateNotificationsUseCase,
    private val saveOnboardingResult: SaveOnboardingResultUseCase,
    private val signOut: SignOutUseCase,
    getCurrentUser: GetCurrentUserUseCase,
) : MviViewModel<SettingsState, SettingsIntent, SettingsEffect>(
    SettingsState(settings = observeSettings().value, username = getCurrentUser()),
) {
    init {
        observeSettings()
            .onEach { settings -> reduce { copy(settings = settings) } }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ThemeSelected -> updateTheme(intent.mode)
            is SettingsIntent.LanguageSelected -> updateLanguage(intent.language)
            is SettingsIntent.NotificationsToggled -> updateNotifications(intent.enabled)
            SettingsIntent.ShowOnboardingAgain -> {
                saveOnboardingResult(false)
                emitEffect(SettingsEffect.Navigate(Route.Onboarding))
            }
            SettingsIntent.SignOutClicked -> {
                signOut()
                emitEffect(SettingsEffect.Navigate(Route.Auth))
            }
        }
    }
}
