package ua.edu.chnu.estatehub.ui.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.usecase.GetListingsUseCase
import ua.edu.chnu.estatehub.domain.usecase.ObserveFavoritesUseCase
import ua.edu.chnu.estatehub.domain.usecase.ToggleFavoriteUseCase

class HomeViewModel(
    private val getListings: GetListingsUseCase,
    observeFavorites: ObserveFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : MviViewModel<HomeState, HomeIntent, Nothing>(HomeState()) {

    init {
        refresh()
        // Обрані зберігаються в settings і спільні з екраном деталей
        observeFavorites()
            .onEach { ids ->
                reduce { copy(favoriteIds = ids) }
                refresh()
            }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectTab -> reduce { copy(tab = intent.tab) }
            is HomeIntent.QueryChanged -> {
                reduce { copy(query = intent.query) }
                refresh()
            }
            is HomeIntent.CategorySelected -> {
                reduce { copy(category = intent.category) }
                refresh()
            }
            is HomeIntent.ToggleFavorite -> toggleFavorite(intent.id)
        }
    }

    private fun refresh() {
        val s = currentState
        val visible = getListings(s.query, s.category)
        val favorites = getListings().filter { it.id in s.favoriteIds }
        reduce { copy(listings = visible, favorites = favorites) }
    }
}
