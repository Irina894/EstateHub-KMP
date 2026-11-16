package ua.edu.chnu.estatehub.locale

expect fun systemLanguageCode(): String


@androidx.compose.runtime.Composable
expect fun ProvideAppLanguage(language: AppLanguage, content: @androidx.compose.runtime.Composable () -> Unit)
