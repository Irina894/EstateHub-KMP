package ua.edu.chnu.estatehub.data

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ua.edu.chnu.estatehub.domain.model.AppSettings
import ua.edu.chnu.estatehub.domain.model.AuthResult
import ua.edu.chnu.estatehub.domain.model.ThemeMode
import ua.edu.chnu.estatehub.domain.repository.AppSettingsRepository
import ua.edu.chnu.estatehub.domain.repository.AuthRepository
import ua.edu.chnu.estatehub.domain.repository.OnboardingRepository
import ua.edu.chnu.estatehub.locale.AppLanguage
import ua.edu.chnu.estatehub.locale.systemLanguageCode

/**
 * Репозиторії поверх multiplatform-settings:
 *  Android -> SharedPreferences, iOS -> NSUserDefaults, Desktop -> java.util.prefs.
 */
class OnboardingRepositoryImpl(private val settings: Settings) : OnboardingRepository {
    private val log = Logger.withTag("OnboardingRepository")

    override fun isCompleted(): Boolean = settings.getBoolean(KEY_COMPLETED, false)

    override fun setCompleted(completed: Boolean) {
        settings.putBoolean(KEY_COMPLETED, completed)
        log.i { "onboardingCompleted = $completed" }
    }

    private companion object {
        const val KEY_COMPLETED = "onboarding_completed"
    }
}

/**
 * Локальна (демо) авторизація: приймає будь-які коректні за форматом облікові дані й запам'ятовує
 * користувача. Інтерфейс AuthRepository залишається тим самим, коли тут з'явиться виклик
 * API (POST /api/auth/login) з backend-частини EstateHub.
 */
class LocalAuthRepository(private val settings: Settings) : AuthRepository {
    private val log = Logger.withTag("AuthRepository")

    override suspend fun signIn(username: String, password: String): AuthResult {
        delay(FAKE_NETWORK_DELAY_MS) // імітація мережевого запиту; delay підтримує скасування
        settings.putString(KEY_USER, username)
        log.i { "signed in as $username" }
        return AuthResult.Success(username)
    }

    override fun isSignedIn(): Boolean = settings.getStringOrNull(KEY_USER) != null

    override fun currentUser(): String? = settings.getStringOrNull(KEY_USER)

    override fun signOut() {
        settings.remove(KEY_USER)
        log.i { "signed out" }
    }

    private companion object {
        const val KEY_USER = "signed_in_user"
        const val FAKE_NETWORK_DELAY_MS = 700L
    }
}

class AppSettingsRepositoryImpl(private val store: Settings) : AppSettingsRepository {
    private val log = Logger.withTag("AppSettings")

    private val _settings = MutableStateFlow(load())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun setThemeMode(mode: ThemeMode) {
        store.putString(KEY_THEME, mode.name)
        _settings.update { it.copy(themeMode = mode) }
        log.i { "themeMode = $mode" }
    }

    override fun setLanguage(language: AppLanguage) {
        store.putString(KEY_LANGUAGE, language.code)
        _settings.update { it.copy(language = language) }
        log.i { "language = ${language.code}" }
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        store.putBoolean(KEY_NOTIFICATIONS, enabled)
        _settings.update { it.copy(notificationsEnabled = enabled) }
    }

    private fun load(): AppSettings {
        val theme = store.getStringOrNull(KEY_THEME)
            ?.let { name -> ThemeMode.entries.firstOrNull { it.name == name } }
            ?: ThemeMode.SYSTEM
        val language = AppLanguage.fromCode(store.getStringOrNull(KEY_LANGUAGE) ?: systemLanguageCode())
        return AppSettings(
            themeMode = theme,
            language = language,
            notificationsEnabled = store.getBoolean(KEY_NOTIFICATIONS, true),
        )
    }

    private companion object {
        const val KEY_THEME = "theme_mode"
        const val KEY_LANGUAGE = "language"
        const val KEY_NOTIFICATIONS = "notifications"
    }
}
