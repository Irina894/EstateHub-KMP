package ua.edu.chnu.estatehub.ui.details

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.usecase.GetListingByIdUseCase
import ua.edu.chnu.estatehub.domain.usecase.ObserveFavoritesUseCase
import ua.edu.chnu.estatehub.domain.usecase.ToggleFavoriteUseCase

class DetailsViewModel(
    private val getListingById: GetListingByIdUseCase,
    observeFavorites: ObserveFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : MviViewModel<DetailsState, DetailsIntent, Nothing>(DetailsState()) {

    init {
        observeFavorites()
            .onEach { ids -> reduce { copy(favoriteIds = ids) } }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.Load -> {
                if (currentState.loaded && currentState.listing?.id == intent.listingId) return
                reduce { copy(listing = getListingById(intent.listingId), loaded = true) }
            }
            DetailsIntent.ToggleFavorite -> currentState.listing?.let { toggleFavorite(it.id) }
        }
    }
}
