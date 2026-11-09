package com.bodnar.carpathians.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Базова ViewModel для MVI:
 *  - [State]  — незмінний стан екрана, який малює UI;
 *  - [Intent] — дії користувача, що надходять з UI в [onIntent];
 *  - [Effect] — одноразові події (навігація, снекбар, фокус), які UI обробляє один раз.
 */
abstract class MviViewModel<State, Intent, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    protected val currentState: State get() = _state.value

    abstract fun onIntent(intent: Intent)

    protected fun setState(reduce: State.() -> State) {
        _state.update(reduce)
    }

    protected fun sendEffect(effect: Effect) {
        _effects.trySend(effect)
    }
}

/** Збирає одноразові події ViewModel, поки екран у композиції. */
@Composable
fun <Effect> CollectEffects(effects: Flow<Effect>, onEffect: suspend (Effect) -> Unit) {
    val handler by rememberUpdatedState(onEffect)
    LaunchedEffect(effects) {
        effects.collect { handler(it) }
    }
}
