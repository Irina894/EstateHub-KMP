package ua.edu.chnu.estatehub.locale

/** Мова системи (код на кшталт "uk" або "en"). Викликається один раз при першому запуску. */
expect fun systemLanguageCode(): String

/**
 * Підставляє обрану мову в локаль платформи й перебудовує вміст,
 * щоб stringResource() одразу підхопив нову мову.
 */
@androidx.compose.runtime.Composable
expect fun ProvideAppLanguage(language: AppLanguage, content: @androidx.compose.runtime.Composable () -> Unit)
