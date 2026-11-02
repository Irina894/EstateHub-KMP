package ua.edu.chnu.estatehub.locale

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

actual fun systemLanguageCode(): String = Locale.getDefault().language

@Composable
actual fun ProvideAppLanguage(language: AppLanguage, content: @Composable () -> Unit) {
    val baseConfiguration = LocalConfiguration.current
    val configuration = remember(baseConfiguration, language) {
        val locale = Locale.forLanguageTag(language.code)
        Locale.setDefault(locale)
        Configuration(baseConfiguration).apply { setLocale(locale) }
    }
    CompositionLocalProvider(LocalConfiguration provides configuration) {
        // key: при зміні мови вміст створюється заново й читає нову локаль
        key(language) { content() }
    }
}
