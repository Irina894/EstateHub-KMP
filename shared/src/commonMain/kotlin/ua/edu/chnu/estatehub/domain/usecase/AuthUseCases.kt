package ua.edu.chnu.estatehub.domain.usecase

import ua.edu.chnu.estatehub.domain.model.AuthResult
import ua.edu.chnu.estatehub.domain.model.PasswordError
import ua.edu.chnu.estatehub.domain.model.UserProfile
import ua.edu.chnu.estatehub.domain.model.UsernameError
import ua.edu.chnu.estatehub.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

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

/** Пароль під час входу лише не порожній: правила складності діють при реєстрації, а не при вході. */
class ValidateLoginPasswordUseCase {
    operator fun invoke(value: String): PasswordError? = if (value.isEmpty()) PasswordError.Empty else null
}

class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): AuthResult =
        repository.signIn(username.trim(), password)
}

class IsSignedInUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Boolean = repository.isAuthorized.value
}

/** Потік ознаки авторизації: стає false, коли користувач вийшов або refresh-токен недійсний. */
class ObserveAuthorizedUseCase(private val repository: AuthRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.isAuthorized
}

/** Перевірка сесії захищеним запитом (запускає автоматичне оновлення токена, якщо потрібно). */
class LoadProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): UserProfile? = repository.loadProfile()
}

class SignOutUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.signOut()
}

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): String? = repository.currentUser()
}
