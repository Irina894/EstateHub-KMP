package ua.edu.chnu.estatehub

import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.ListingCategory
import ua.edu.chnu.estatehub.domain.model.PasswordError
import ua.edu.chnu.estatehub.domain.model.UsernameError
import ua.edu.chnu.estatehub.domain.repository.ListingsRepository
import ua.edu.chnu.estatehub.domain.repository.OnboardingRepository
import ua.edu.chnu.estatehub.domain.usecase.GetListingByIdUseCase
import ua.edu.chnu.estatehub.domain.usecase.GetListingsUseCase
import ua.edu.chnu.estatehub.domain.usecase.IsOnboardingCompletedUseCase
import ua.edu.chnu.estatehub.domain.usecase.SaveOnboardingResultUseCase
import ua.edu.chnu.estatehub.domain.usecase.ValidatePasswordUseCase
import ua.edu.chnu.estatehub.domain.usecase.ValidateUsernameUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidationUseCaseTests {
    private val username = ValidateUsernameUseCase()
    private val password = ValidatePasswordUseCase()

    @Test
    fun usernameRules() {
        assertEquals(UsernameError.Empty, username("   "))
        assertEquals(UsernameError.TooShort, username("ab"))
        assertEquals(UsernameError.TooLong, username("a".repeat(33)))
        assertEquals(UsernameError.InvalidChars, username("iryna sd"))
        assertNull(username("iryna_1"))
        assertNull(username("Ірина.Д"))
    }

    @Test
    fun passwordRules() {
        assertEquals(PasswordError.Empty, password(""))
        assertEquals(PasswordError.TooShort, password("abc1"))
        assertEquals(PasswordError.NoLetter, password("12345678"))
        assertEquals(PasswordError.NoDigit, password("abcdefgh"))
        assertNull(password("abcdefg1"))
    }
}

private class FakeOnboardingRepository(var completed: Boolean = false) : OnboardingRepository {
    override fun isCompleted() = completed
    override fun setCompleted(completed: Boolean) {
        this.completed = completed
    }
}

class OnboardingUseCaseTests {
    @Test
    fun saveThenCheck() {
        val repo = FakeOnboardingRepository()
        val isCompleted = IsOnboardingCompletedUseCase(repo)
        val save = SaveOnboardingResultUseCase(repo)

        assertFalse(isCompleted())
        save()
        assertTrue(isCompleted())
        save(false)
        assertFalse(isCompleted())
    }
}

private class FakeListingsRepository : ListingsRepository {
    override fun getAll() = listOf(
        Listing(1, ListingCategory.APARTMENT, 85_000, "Чернівці", "вул. Героїв Майдану, 12", 54, rooms = 2),
        Listing(2, ListingCategory.HOUSE, 142_000, "Львів", "вул. Зелена, 88", 128, rooms = 4),
    )

    override fun getById(id: Long) = getAll().firstOrNull { it.id == id }
}

class GetListingsUseCaseTests {
    private val getListings = GetListingsUseCase(FakeListingsRepository())

    @Test
    fun filtersByQueryAndCategory() {
        assertEquals(2, getListings().size)
        assertEquals(listOf(2L), getListings(query = "львів").map { it.id })
        assertEquals(listOf(1L), getListings(category = ListingCategory.APARTMENT).map { it.id })
        assertTrue(getListings(query = "Київ").isEmpty())
    }

    @Test
    fun pricePerSquareMeter() {
        assertEquals(1574, getListings().first().pricePerSqm)
    }
}

class GetListingByIdUseCaseTests {
    private val getById = GetListingByIdUseCase(FakeListingsRepository())

    @Test
    fun returnsListingOrNull() {
        assertEquals("Львів", getById(2)?.city)
        assertNull(getById(99))
    }
}
