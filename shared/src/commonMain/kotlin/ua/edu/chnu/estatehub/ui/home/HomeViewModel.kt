package ua.edu.chnu.estatehub.ui.home

import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.usecase.GetListingsUseCase

class HomeViewModel(
    private val getListings: GetListingsUseCase,
) : MviViewModel<HomeState, HomeIntent, Nothing>(HomeState()) {

    init {
        refresh()
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
            is HomeIntent.ToggleFavorite -> {
                val ids = currentState.favoriteIds
                reduce { copy(favoriteIds = if (intent.id in ids) ids - intent.id else ids + intent.id) }
                refresh()
            }
        }
    }

    private fun refresh() {
        val s = currentState
        val visible = getListings(s.query, s.category)
        val favorites = getListings().filter { it.id in s.favoriteIds }
        reduce { copy(listings = visible, favorites = favorites) }
    }
}
