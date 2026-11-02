package ua.edu.chnu.estatehub.ui.onboarding

import co.touchlab.kermit.Logger
import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.usecase.IsSignedInUseCase
import ua.edu.chnu.estatehub.domain.usecase.SaveOnboardingResultUseCase
import ua.edu.chnu.estatehub.navigation.Route

class OnboardingViewModel(
    private val saveOnboardingResult: SaveOnboardingResultUseCase,
    private val isSignedIn: IsSignedInUseCase,
) : MviViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(OnboardingState()) {

    private val log = Logger.withTag("Onboarding")

    override fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.PageChanged -> {
                val page = intent.page.coerceIn(0, currentState.pageCount - 1)
                if (page != currentState.page) {
                    log.i { "Page ${page + 1} of ${currentState.pageCount}" }
                    reduce { copy(page = page) }
                }
            }
            OnboardingIntent.NextClicked ->
                if (currentState.isLastPage) finish() else reduce { copy(page = page + 1) }
            OnboardingIntent.SkipClicked -> {
                log.i { "Onboarding skipped on page ${currentState.page + 1}" }
                finish()
            }
        }
    }

    private fun finish() {
        saveOnboardingResult(true)
        // Якщо onboarding відкрили повторно з налаштувань, користувач уже увійшов
        val next = if (isSignedIn()) Route.Home else Route.Auth
        emitEffect(OnboardingEffect.Navigate(next))
    }
}
