package ua.edu.chnu.estatehub.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ua.edu.chnu.estatehub.core.mvi.CollectEffects
import ua.edu.chnu.estatehub.domain.model.AuthFailure
import ua.edu.chnu.estatehub.domain.model.PasswordError
import ua.edu.chnu.estatehub.domain.model.UsernameError
import ua.edu.chnu.estatehub.navigation.Route
import ua.edu.chnu.estatehub.resources.*

@Composable
fun AuthRoute(
    viewModel: AuthViewModel,
    onNavigate: (Route) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            AuthEffect.NavigateToHome -> onNavigate(Route.Home)
        }
    }
    AuthScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun AuthScreen(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val passwordFocus = remember { FocusRequester() }
    val usernameError = state.usernameError
    val passwordError = state.passwordError

    // Одна дія для кнопки "Увійти" і для Done на клавіатурі в полі пароля
    val submit = {
        keyboard?.hide()
        focusManager.clearFocus()
        onIntent(AuthIntent.SignInClicked)
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            AuthHero()

            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = state.username,
                    onValueChange = { onIntent(AuthIntent.UsernameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(Res.string.auth_username)) },
                    leadingIcon = {
                        Icon(painterResource(Res.drawable.ic_person), contentDescription = null)
                    },
                    isError = state.usernameError != null,
                    supportingText = if (usernameError != null) {
                        { Text(usernameError.message()) }
                    } else null,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    // Next у полі імені: перевірити ім'я й перейти до пароля
                    keyboardActions = KeyboardActions(onNext = {
                        onIntent(AuthIntent.UsernameSubmitted)
                        passwordFocus.requestFocus()
                    }),
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
                    modifier = Modifier.fillMaxWidth().focusRequester(passwordFocus),
                    label = { Text(stringResource(Res.string.auth_password)) },
                    leadingIcon = {
                        Icon(painterResource(Res.drawable.ic_lock), contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { onIntent(AuthIntent.TogglePasswordVisibility) }) {
                            Icon(
                                painter = painterResource(
                                    if (state.isPasswordVisible) Res.drawable.ic_visibility_off
                                    else Res.drawable.ic_visibility
                                ),
                                contentDescription = stringResource(Res.string.auth_toggle_password),
                            )
                        }
                    },
                    isError = state.passwordError != null,
                    supportingText = if (passwordError != null) {
                        { Text(passwordError.message()) }
                    } else null,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    // Done у полі пароля: те саме, що кнопка "Увійти"
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                )

                state.failure?.let { failure ->
                    Text(
                        text = failure.message(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Button(
                    onClick = { submit() },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.auth_sign_in),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                Text(
                    text = stringResource(Res.string.auth_demo_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Верхня панель з "архітектурним" силуетом міста, намальованим на Canvas. */
@Composable
private fun AuthHero() {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val sun = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 260.dp, max = 320.dp)
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(primary),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // сонце
            drawCircle(color = sun.copy(alpha = 0.92f), radius = h * 0.13f, center = Offset(w * 0.80f, h * 0.30f))

            // будинки: (x, ширина, висота) у частках розміру полотна
            val skyline = listOf(
                Triple(0.04f, 0.16f, 0.34f),
                Triple(0.22f, 0.20f, 0.52f),
                Triple(0.45f, 0.14f, 0.30f),
                Triple(0.62f, 0.18f, 0.44f),
                Triple(0.83f, 0.14f, 0.26f),
            )
            skyline.forEachIndexed { index, (x, bw, bh) ->
                val left = w * x
                val bWidth = w * bw
                val bHeight = h * bh
                val top = h - bHeight
                drawRect(
                    color = onPrimary.copy(alpha = if (index % 2 == 0) 0.14f else 0.09f),
                    topLeft = Offset(left, top),
                    size = Size(bWidth, bHeight),
                )
                // вікна
                val cols = 3
                val rows = (bHeight / (h * 0.085f)).toInt().coerceAtLeast(1)
                val cellW = bWidth / (cols + 1)
                val cellH = bHeight / (rows + 1)
                for (r in 1..rows) {
                    for (c in 1..cols) {
                        drawRect(
                            color = onPrimary.copy(alpha = 0.22f),
                            topLeft = Offset(left + cellW * c - cellW * 0.22f, top + cellH * r - cellH * 0.2f),
                            size = Size(cellW * 0.44f, cellH * 0.4f),
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(onPrimary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_home),
                        contentDescription = null,
                        tint = onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.size(10.dp))
                Text(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = onPrimary,
                )
            }
            Column {
                Text(
                    text = stringResource(Res.string.auth_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = onPrimary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(Res.string.auth_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = onPrimary.copy(alpha = 0.82f),
                )
                Spacer(Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun UsernameError.message(): String = stringResource(
    when (this) {
        UsernameError.Empty -> Res.string.error_username_empty
        UsernameError.TooShort -> Res.string.error_username_short
        UsernameError.TooLong -> Res.string.error_username_long
        UsernameError.InvalidChars -> Res.string.error_username_chars
    }
)

@Composable
private fun PasswordError.message(): String = stringResource(
    when (this) {
        PasswordError.Empty -> Res.string.error_password_empty
        PasswordError.TooShort -> Res.string.error_password_short
        PasswordError.NoLetter -> Res.string.error_password_letter
        PasswordError.NoDigit -> Res.string.error_password_digit
    }
)

@Composable
private fun AuthFailure.message(): String = stringResource(
    when (this) {
        AuthFailure.InvalidCredentials -> Res.string.error_invalid_credentials
        AuthFailure.Network -> Res.string.error_network
    }
)
