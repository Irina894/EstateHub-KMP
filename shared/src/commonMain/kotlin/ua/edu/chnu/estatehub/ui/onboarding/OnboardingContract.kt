package ua.edu.chnu.estatehub.ui.onboarding

import ua.edu.chnu.estatehub.navigation.Route

data class OnboardingState(
    val page: Int = 0,
    val pageCount: Int = onboardingPages.size,
) {
    val isLastPage: Boolean get() = page >= pageCount - 1
}

sealed interface OnboardingIntent {
    data object NextClicked : OnboardingIntent
    data object SkipClicked : OnboardingIntent
    data class PageChanged(val page: Int) : OnboardingIntent
}

sealed interface OnboardingEffect {
    /** Onboarding завершено, куди йти далі. */
    data class Navigate(val route: Route) : OnboardingEffect
}
