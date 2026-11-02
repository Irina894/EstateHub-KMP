package ua.edu.chnu.estatehub.ui.home

import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.ListingCategory

enum class HomeTab { SEARCH, FAVORITES, SETTINGS }

data class HomeState(
    val tab: HomeTab = HomeTab.SEARCH,
    val query: String = "",
    val category: ListingCategory? = null,
    val listings: List<Listing> = emptyList(),
    val favoriteIds: Set<Long> = emptySet(),
    val favorites: List<Listing> = emptyList(),
)

sealed interface HomeIntent {
    data class SelectTab(val tab: HomeTab) : HomeIntent
    data class QueryChanged(val query: String) : HomeIntent
    data class CategorySelected(val category: ListingCategory?) : HomeIntent
    data class ToggleFavorite(val id: Long) : HomeIntent
}
