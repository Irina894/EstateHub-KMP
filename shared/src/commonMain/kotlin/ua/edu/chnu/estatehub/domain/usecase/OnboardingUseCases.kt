package ua.edu.chnu.estatehub.domain.usecase

import ua.edu.chnu.estatehub.domain.repository.OnboardingRepository

/** Чи пройшов користувач onboarding. */
class IsOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Boolean = repository.isCompleted()
}

/** Зберігає результат onboarding (completed = true) або скидає його (false). */
class SaveOnboardingResultUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(completed: Boolean = true) = repository.setCompleted(completed)
}
