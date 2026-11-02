package ua.edu.chnu.estatehub.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import ua.edu.chnu.estatehub.domain.model.AppSettings
import ua.edu.chnu.estatehub.domain.model.ThemeMode
import ua.edu.chnu.estatehub.domain.repository.AppSettingsRepository
import ua.edu.chnu.estatehub.locale.AppLanguage

class ObserveAppSettingsUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(): StateFlow<AppSettings> = repository.settings
}

class UpdateThemeModeUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}

class UpdateLanguageUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(language: AppLanguage) = repository.setLanguage(language)
}

class UpdateNotificationsUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(enabled: Boolean) = repository.setNotificationsEnabled(enabled)
}
