package ua.edu.chnu.estatehub.domain.usecase

import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.ListingCategory
import ua.edu.chnu.estatehub.domain.repository.ListingsRepository

/** Оголошення з фільтром за текстом (місто/вулиця) і категорією. */
class GetListingsUseCase(private val repository: ListingsRepository) {
    operator fun invoke(query: String = "", category: ListingCategory? = null): List<Listing> {
        val text = query.trim()
        return repository.getAll().filter { listing ->
            (category == null || listing.category == category) &&
                (text.isEmpty() || listing.address.contains(text, ignoreCase = true))
        }
    }
}
