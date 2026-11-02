package ua.edu.chnu.estatehub.data

import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.ListingCategory
import ua.edu.chnu.estatehub.domain.model.ListingDetails
import ua.edu.chnu.estatehub.domain.model.Realtor
import ua.edu.chnu.estatehub.domain.repository.ListingsRepository

/** Тимчасові дані. У лабораторній про REST API тут буде запит до Catalog API (GET /api/Properties). */
class InMemoryListingsRepository : ListingsRepository {

    private val listings = listOf(
        Listing(
            1, ListingCategory.APARTMENT, 85_000, "Чернівці", "вул. Героїв Майдану, 12", 54,
            rooms = 2, floor = 3, totalFloors = 7,
            details = ListingDetails(
                description = "Світла та тепла двокімнатна квартира зі свіжим якісним ремонтом у затишному районі міста. " +
                    "Продумане планування з окремими кімнатами, новими енергоощадними вікнами та автономним опаленням. " +
                    "Повністю укомплектована всіма необхідними меблями й надійною технікою, готова до комфортного заселення.",
                buildingType = "Цегла й керамоблок",
                heating = "Індивідуальне газове",
                condition = "Авторський ремонт",
                yearBuilt = 2022,
                realtor = Realtor("Олена Ковальчук", "+380 67 123 45 67"),
            ),
        ),
        Listing(
            2, ListingCategory.HOUSE, 142_000, "Львів", "вул. Зелена, 88", 128,
            rooms = 4, totalFloors = 2, landAres = 6,
            details = ListingDetails(
                description = "Просторий двоповерховий будинок із власним подвір’ям і гаражем. Тиха вулиця, розвинена інфраструктура, " +
                    "до центру 15 хвилин на авто.",
                buildingType = "Цегла",
                heating = "Газовий котел",
                condition = "Житловий стан",
                yearBuilt = 2015,
                realtor = Realtor("Андрій Мельник", "+380 50 222 33 44"),
            ),
        ),
        Listing(
            3, ListingCategory.APARTMENT, 62_000, "Київ", "вул. Рейтарська, 19", 41,
            rooms = 1, floor = 4, totalFloors = 9,
            details = ListingDetails(
                description = "Компактна однокімнатна квартира в історичному центрі. Високі стелі, великі вікна з видом на місто, " +
                    "поруч метро та кафе.",
                buildingType = "Монолітно-каркасний",
                heating = "Централізоване",
                condition = "Євроремонт",
                yearBuilt = 2018,
                realtor = Realtor("Софія Лисенко", "+380 63 555 66 77"),
            ),
        ),
        Listing(
            4, ListingCategory.LAND, 38_000, "Вижниця", "вул. Лісова, 4", 1_000, landAres = 10,
            details = ListingDetails(
                description = "Рівна ділянка під забудову біля лісу. Комунікації поруч, під’їзд із твердим покриттям, мальовничі краєвиди.",
                condition = "Під забудову",
                realtor = Realtor("Тарас Бойко", "+380 97 888 99 00"),
            ),
        ),
        Listing(
            5, ListingCategory.HOUSE, 97_500, "Чернівці", "вул. Шептицького, 31", 96,
            rooms = 3, totalFloors = 1, landAres = 5,
            details = ListingDetails(
                description = "Одноповерховий будинок із доглянутим садом та літньою кухнею. Окрема парковка на два авто.",
                buildingType = "Газобетон",
                heating = "Електричне",
                condition = "Свіжий ремонт",
                yearBuilt = 2020,
                realtor = Realtor("Олена Ковальчук", "+380 67 123 45 67"),
            ),
        ),
    )

    override fun getAll(): List<Listing> = listings

    override fun getById(id: Long): Listing? = listings.firstOrNull { it.id == id }
}
