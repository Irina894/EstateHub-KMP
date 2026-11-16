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
<<<<<<< HEAD
    val failure: AuthFailure? = null,
=======
    /** Чи була спроба входу: після неї помилки оновлюються під час введення. */
>>>>>>> Lab 6: ktor networking, KSafe token storage, auto refresh, MVI auth with snackbar, auth-aware navigation
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
    /** Одноразова подія: показати спливний snackbar із помилкою входу. */
    data class ShowError(val failure: AuthFailure) : AuthEffect
}
