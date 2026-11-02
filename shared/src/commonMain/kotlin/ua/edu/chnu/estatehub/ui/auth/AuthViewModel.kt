package ua.edu.chnu.estatehub.ui.auth

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import ua.edu.chnu.estatehub.core.mvi.MviViewModel
import ua.edu.chnu.estatehub.domain.model.AuthFailure
import ua.edu.chnu.estatehub.domain.model.AuthResult
import ua.edu.chnu.estatehub.domain.usecase.SignInUseCase
import ua.edu.chnu.estatehub.domain.usecase.ValidatePasswordUseCase
import ua.edu.chnu.estatehub.domain.usecase.ValidateUsernameUseCase

class AuthViewModel(
    private val validateUsername: ValidateUsernameUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val signIn: SignInUseCase,
) : MviViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    private val log = Logger.withTag("Auth")

    override fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UsernameChanged -> reduce {
                copy(
                    username = intent.value,
                    usernameError = if (submitted || usernameError != null) validateUsername(intent.value) else null,
                    failure = null,
                )
            }
            is AuthIntent.PasswordChanged -> reduce {
                copy(
                    password = intent.value,
                    passwordError = if (submitted || passwordError != null) validatePassword(intent.value) else null,
                    failure = null,
                )
            }
            AuthIntent.UsernameSubmitted -> reduce { copy(usernameError = validateUsername(username)) }
            AuthIntent.TogglePasswordVisibility -> reduce { copy(isPasswordVisible = !isPasswordVisible) }
            AuthIntent.SignInClicked -> submit()
        }
    }

    private fun submit() {
        val s = currentState
        if (s.isLoading) return

        val usernameError = validateUsername(s.username)
        val passwordError = validatePassword(s.password)
        if (usernameError != null || passwordError != null) {
            log.i { "Validation failed: $usernameError / $passwordError" }
            reduce { copy(submitted = true, usernameError = usernameError, passwordError = passwordError) }
            return
        }

        reduce { copy(submitted = true, isLoading = true, failure = null) }
        viewModelScope.launch {
            try {
                when (val result = signIn(s.username, s.password)) {
                    is AuthResult.Success -> {
                        reduce { copy(isLoading = false) }
                        emitEffect(AuthEffect.NavigateToHome)
                    }
                    is AuthResult.Failure -> reduce { copy(isLoading = false, failure = result.reason) }
                }
            } catch (e: CancellationException) {
                throw e // скасування корутини не є помилкою входу
            } catch (e: Exception) {
                log.e(e) { "Sign in failed" }
                reduce { copy(isLoading = false, failure = AuthFailure.Network) }
            }
        }
    }
}
