package ua.edu.chnu.estatehub.navigation

/** Екрани застосунку (ключі Navigation 3). */
sealed interface Route {
    data object Onboarding : Route
    data object Auth : Route
    data object Home : Route
    /** Екран деталей: параметр передається прямо в ключі навігації. */
    data class Details(val listingId: Long) : Route
}
