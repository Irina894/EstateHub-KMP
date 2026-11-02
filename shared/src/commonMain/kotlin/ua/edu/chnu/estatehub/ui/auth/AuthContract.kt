package ua.edu.chnu.estatehub.ui.auth

import ua.edu.chnu.estatehub.domain.model.AuthFailure
import ua.edu.chnu.estatehub.domain.model.PasswordError
import ua.edu.chnu.estatehub.domain.model.UsernameError

data class AuthState(
    val username: String = "",
    val password: String = "",
    val usernameError: UsernameError? = null,
    val passwordError: PasswordError? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val failure: AuthFailure? = null,
    /** Чи була спроба входу: після неї помилки оновлюються під час введення. */
    val submitted: Boolean = false,
)

sealed interface AuthIntent {
    data class UsernameChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    /** Клавіатура: Next у полі імені. Перевіряє ім'я й переходить до пароля. */
    data object UsernameSubmitted : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    /** Кнопка "Увійти" або Done на клавіатурі у полі пароля. */
    data object SignInClicked : AuthIntent
}

sealed interface AuthEffect {
    data object NavigateToHome : AuthEffect
}
