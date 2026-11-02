package ua.edu.chnu.estatehub.ui.details

import ua.edu.chnu.estatehub.domain.model.Listing

data class DetailsState(
    val listing: Listing? = null,
    val loaded: Boolean = false,
    val favoriteIds: Set<Long> = emptySet(),
) {
    val isFavorite: Boolean get() = listing != null && listing.id in favoriteIds
}

sealed interface DetailsIntent {
    data class Load(val listingId: Long) : DetailsIntent
    data object ToggleFavorite : DetailsIntent
}
