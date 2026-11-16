package ua.edu.chnu.estatehub.domain.usecase

import ua.edu.chnu.estatehub.domain.repository.OnboardingRepository


class IsOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Boolean = repository.isCompleted()
}
class SaveOnboardingResultUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(completed: Boolean = true) = repository.setCompleted(completed)
}
