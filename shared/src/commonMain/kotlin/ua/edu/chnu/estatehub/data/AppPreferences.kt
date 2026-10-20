package ua.edu.chnu.estatehub.data

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings

class AppPreferences(private val settings: Settings = Settings()) {

    private val log = Logger.withTag("AppPreferences")

    var onboardingCompleted: Boolean
        get() = settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) {
            settings.putBoolean(KEY_ONBOARDING_COMPLETED, value)
            log.i { "onboardingCompleted = $value" }
        }

    var darkThemeOverride: Boolean?
        get() = settings.getBooleanOrNull(KEY_DARK_THEME)
        set(value) {
            if (value == null) settings.remove(KEY_DARK_THEME) else settings.putBoolean(KEY_DARK_THEME, value)
            log.i { "darkThemeOverride = $value" }
        }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_DARK_THEME = "dark_theme"
    }
}