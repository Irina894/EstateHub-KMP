package ua.edu.chnu.estatehub.navigation

/** Екрани застосунку (ключі Navigation 3). */
sealed interface Route {
    data object Onboarding : Route
    data object Auth : Route
    data object Home : Route
}
