package com.stedis.navigation.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationManager(
    private val initialState: NavigationState,
) {

    private val _stateFlow = MutableStateFlow(initialState)

    public val stateFlow: StateFlow<NavigationState>
        get() = _stateFlow.asStateFlow()

    public val currentState: NavigationState
        get() = _stateFlow.value

    internal fun updateState(state: NavigationState) {
        _stateFlow.value = state
    }
}

public fun NavigationManager(state: () -> NavigationState) = NavigationManager(state())

public fun NavigationManager.execute(command: NavigationCommand) {
    updateState(command.execute(currentState))
}