package ua.edu.chnu.estatehub.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ua.edu.chnu.estatehub.domain.model.AppSettings
import ua.edu.chnu.estatehub.domain.model.AuthResult
import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.ThemeMode
import ua.edu.chnu.estatehub.locale.AppLanguage

/** Результат проходження onboarding (зберігається через multiplatform-settings). */
interface OnboardingRepository {
    fun isCompleted(): Boolean
    fun setCompleted(completed: Boolean)
}

interface AuthRepository {
    suspend fun signIn(username: String, password: String): AuthResult
    fun isSignedIn(): Boolean
    fun currentUser(): String?
    fun signOut()
}

interface AppSettingsRepository {
    val settings: StateFlow<AppSettings>
    fun setThemeMode(mode: ThemeMode)
    fun setLanguage(language: AppLanguage)
    fun setNotificationsEnabled(enabled: Boolean)
}

interface ListingsRepository {
    fun getAll(): List<Listing>
}
