package ua.edu.chnu.estatehub.domain.model

import ua.edu.chnu.estatehub.locale.AppLanguage

enum class ThemeMode { LIGHT, DARK, SYSTEM }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val notificationsEnabled: Boolean = true,
)

enum class UsernameError { Empty, TooShort, TooLong, InvalidChars }

enum class PasswordError { Empty, TooShort, NoLetter, NoDigit }

enum class AuthFailure { InvalidCredentials, Network }

sealed interface AuthResult {
    data class Success(val username: String) : AuthResult
    data class Failure(val reason: AuthFailure) : AuthResult
}

enum class ListingCategory { APARTMENT, HOUSE, LAND }

/** Рієлтор, відповідальний за оголошення. */
data class Realtor(val name: String, val phone: String)

/** Розширений опис оголошення (екран деталей). */
data class ListingDetails(
    val description: String = "",
    val buildingType: String = "",
    val heating: String = "",
    val condition: String = "",
    val yearBuilt: Int? = null,
    val realtor: Realtor? = null,
)

/** Модель оголошення. Поля відповідають сутності Property з Catalog API (лаби .NET). */
data class Listing(
    val id: Long,
    val category: ListingCategory,
    val priceUsd: Int,
    val city: String,
    val street: String,
    val areaM2: Int,
    val rooms: Int? = null,
    val floor: Int? = null,
    val totalFloors: Int? = null,
    val landAres: Int? = null,
    val details: ListingDetails = ListingDetails(),
) {
    val pricePerSqm: Int get() = if (areaM2 > 0) priceUsd / areaM2 else 0
    val address: String get() = "$city, $street"
}
