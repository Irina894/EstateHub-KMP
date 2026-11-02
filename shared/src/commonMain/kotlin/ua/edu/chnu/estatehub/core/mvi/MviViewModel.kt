package ua.edu.chnu.estatehub.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Базовий ViewModel для MVI.
 *  - State  : єдине незмінне джерело правди для UI (StateFlow);
 *  - Intent : дія користувача або системи, яку UI передає у onIntent();
 *  - Effect : одноразова подія (навігація тощо), яку UI споживає один раз.
 */
abstract class MviViewModel<S : Any, I : Any, E : Any>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    fun onIntent(intent: I) = handleIntent(intent)

    protected abstract fun handleIntent(intent: I)

    /** Єдине місце, де змінюється стан: чиста функція "старий стан -> новий стан". */
    protected fun reduce(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    protected fun emitEffect(effect: E) {
        viewModelScope.launch { _effects.send(effect) }
    }
}

/** Підписка на одноразові ефекти з Compose. */
@Composable
fun <E> CollectEffects(effects: Flow<E>, onEffect: (E) -> Unit) {
    val latest by rememberUpdatedState(onEffect)
    LaunchedEffect(effects) {
        effects.collect { latest(it) }
    }
}
