package ua.edu.chnu.estatehub.ui.main

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.lifecycle.viewModelScope
import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.model.AppSettings
import ua.edu.chnu.estatehub.domain.usecase.IsOnboardingCompletedUseCase
import ua.edu.chnu.estatehub.domain.usecase.IsSignedInUseCase
import ua.edu.chnu.estatehub.domain.usecase.ObserveAppSettingsUseCase
import ua.edu.chnu.estatehub.navigation.Route

data class MainState(
    val settings: AppSettings,
    /** Стек екранів. Порожній, доки не визначено стартовий екран. */
    val backStack: List<Route> = emptyList(),
)

sealed interface MainIntent {
    /** Замінити весь стек одним екраном (після onboarding, входу, виходу). */
    data class ReplaceAll(val route: Route) : MainIntent
    data class Push(val route: Route) : MainIntent
    data object Back : MainIntent
}

/**
 * Головний ViewModel: визначає стартовий екран за результатом onboarding і входу,
 * зберігає стек навігації та глобальні налаштування (тема, мова).
 */
class MainViewModel(
    observeSettings: ObserveAppSettingsUseCase,
    isOnboardingCompleted: IsOnboardingCompletedUseCase,
    isSignedIn: IsSignedInUseCase,
) : MviViewModel<MainState, MainIntent, Nothing>(
    MainState(settings = observeSettings().value),
) {
    private val log = Logger.withTag("Main")

    init {
        val start = when {
            !isOnboardingCompleted() -> Route.Onboarding
            !isSignedIn() -> Route.Auth
            else -> Route.Home
        }
        log.i { "Start destination: $start" }
        reduce { copy(backStack = listOf(start)) }

        observeSettings()
            .onEach { settings -> reduce { copy(settings = settings) } }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.ReplaceAll -> reduce { copy(backStack = listOf(intent.route)) }
            is MainIntent.Push -> reduce { copy(backStack = backStack + intent.route) }
            MainIntent.Back -> reduce {
                if (backStack.size > 1) copy(backStack = backStack.dropLast(1)) else this
            }
        }
    }
}
