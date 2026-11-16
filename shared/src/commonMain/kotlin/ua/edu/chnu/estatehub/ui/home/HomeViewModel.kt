package ua.edu.chnu.estatehub.ui.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.usecase.GetListingsUseCase
import ua.edu.chnu.estatehub.domain.usecase.ObserveFavoritesUseCase
import ua.edu.chnu.estatehub.domain.usecase.ToggleFavoriteUseCase

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val getListings: GetListingsUseCase,
    observeFavorites: ObserveFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : MviViewModel<HomeState, HomeIntent, Nothing>(HomeState()) {

    private val queryFlow = MutableStateFlow("")

    init {
        refresh()
        // пошук із затримкою: список перераховується, коли користувач зробив паузу в наборі
        queryFlow
            .debounce(300)
            .distinctUntilChanged()
            .onEach { refresh() }
            .launchIn(viewModelScope)
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
                reduce { copy(query = intent.query) } // поле вводу оновлюється одразу
                queryFlow.value = intent.query        // фільтрація - після debounce
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
