package ua.edu.chnu.estatehub.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import java.util.Locale

actual fun systemLanguageCode(): String = Locale.getDefault().language

@Composable
actual fun ProvideAppLanguage(language: AppLanguage, content: @Composable () -> Unit) {
    remember(language) { Locale.setDefault(Locale.forLanguageTag(language.code)) }
    // key: при зміні мови вміст створюється заново й читає нову локаль
    key(language) { content() }
}
