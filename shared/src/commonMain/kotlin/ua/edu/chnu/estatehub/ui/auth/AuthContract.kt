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
    val submitted: Boolean = false,
)

sealed interface AuthIntent {
    data class UsernameChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    data object UsernameSubmitted : AuthIntent
    data object UsernameFocusLost : AuthIntent
    data object PasswordFocusLost : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    data object SignInClicked : AuthIntent
}

sealed interface AuthEffect {
    data object NavigateToHome : AuthEffect
}
