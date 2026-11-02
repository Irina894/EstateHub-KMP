package ua.edu.chnu.estatehub.data

import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.ListingCategory
import ua.edu.chnu.estatehub.domain.repository.ListingsRepository

/** Тимчасові дані. У лабораторній про REST API тут буде запит до Catalog API (GET /api/Properties). */
class InMemoryListingsRepository : ListingsRepository {
    override fun getAll(): List<Listing> = listOf(
        Listing(1, ListingCategory.APARTMENT, 85_000, "Чернівці", "вул. Героїв Майдану, 12", 54, rooms = 2, floor = 3, totalFloors = 7),
        Listing(2, ListingCategory.HOUSE, 142_000, "Львів", "вул. Зелена, 88", 128, rooms = 4, totalFloors = 2, landAres = 6),
        Listing(3, ListingCategory.APARTMENT, 62_000, "Київ", "вул. Рейтарська, 19", 41, rooms = 1, floor = 4, totalFloors = 9),
        Listing(4, ListingCategory.LAND, 38_000, "Вижниця", "вул. Лісова, 4", 1_000, landAres = 10),
        Listing(5, ListingCategory.HOUSE, 97_500, "Чернівці", "вул. Шептицького, 31", 96, rooms = 3, totalFloors = 1, landAres = 5),
    )
}
