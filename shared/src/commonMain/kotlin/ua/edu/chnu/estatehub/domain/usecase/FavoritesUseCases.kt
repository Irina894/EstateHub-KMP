package ua.edu.chnu.estatehub.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import ua.edu.chnu.estatehub.domain.repository.FavoritesRepository

class ObserveFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): StateFlow<Set<Long>> = repository.favoriteIds
}

class ToggleFavoriteUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(id: Long) = repository.toggle(id)
}
