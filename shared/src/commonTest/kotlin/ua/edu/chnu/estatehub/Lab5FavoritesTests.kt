package ua.edu.chnu.estatehub

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.edu.chnu.estatehub.domain.repository.FavoritesRepository
import ua.edu.chnu.estatehub.domain.usecase.ObserveFavoritesUseCase
import ua.edu.chnu.estatehub.domain.usecase.ToggleFavoriteUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeFavoritesRepository : FavoritesRepository {
    private val ids = MutableStateFlow<Set<Long>>(emptySet())
    override val favoriteIds: StateFlow<Set<Long>> = ids
    override fun toggle(id: Long) {
        ids.value = if (id in ids.value) ids.value - id else ids.value + id
    }
}

class FavoritesUseCaseTests {
    @Test
    fun toggleAddsAndRemoves() {
        val repo = FakeFavoritesRepository()
        val observe = ObserveFavoritesUseCase(repo)
        val toggle = ToggleFavoriteUseCase(repo)

        toggle(1)
        toggle(3)
        assertEquals(setOf(1L, 3L), observe().value)
        toggle(1)
        assertEquals(setOf(3L), observe().value)
    }
}
