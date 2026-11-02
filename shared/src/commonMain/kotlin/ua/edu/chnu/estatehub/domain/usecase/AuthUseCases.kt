package ua.edu.chnu.estatehub.domain.usecase

import ua.edu.chnu.estatehub.domain.model.AuthResult
import ua.edu.chnu.estatehub.domain.model.PasswordError
import ua.edu.chnu.estatehub.domain.model.UsernameError
import ua.edu.chnu.estatehub.domain.repository.AuthRepository

/** Ім'я користувача: 3..32 символи, літери/цифри та . _ - */
class ValidateUsernameUseCase {
    operator fun invoke(value: String): UsernameError? {
        val name = value.trim()
        return when {
            name.isEmpty() -> UsernameError.Empty
            name.length < MIN -> UsernameError.TooShort
            name.length > MAX -> UsernameError.TooLong
            !name.all { it.isLetterOrDigit() || it in ALLOWED } -> UsernameError.InvalidChars
            else -> null
        }
    }

    private companion object {
        const val MIN = 3
        const val MAX = 32
        const val ALLOWED = "._-"
    }
}

/** Пароль: щонайменше 8 символів, хоча б одна літера й одна цифра. */
class ValidatePasswordUseCase {
    operator fun invoke(value: String): PasswordError? = when {
        value.isEmpty() -> PasswordError.Empty
        value.length < MIN -> PasswordError.TooShort
        value.none { it.isLetter() } -> PasswordError.NoLetter
        value.none { it.isDigit() } -> PasswordError.NoDigit
        else -> null
    }

    private companion object {
        const val MIN = 8
    }
}

class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): AuthResult =
        repository.signIn(username.trim(), password)
}

class IsSignedInUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Boolean = repository.isSignedIn()
}

class SignOutUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.signOut()
}

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): String? = repository.currentUser()
}
