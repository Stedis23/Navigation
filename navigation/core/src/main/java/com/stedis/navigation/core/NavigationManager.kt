package com.stedis.navigation.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [NavigationManager] manages the navigation state within the application, allowing for updates and modifications
 * through [NavigationCommand].
 *
 * @property initialState The initial [NavigationState] to set for the manager.
 */
class NavigationManager(
    private val initialState: NavigationState,
) {

    private val _stateFlow = MutableStateFlow(initialState)

    /**
     * A public read-only state flow that exposes the current navigation state.
     */
    public val stateFlow: StateFlow<NavigationState>
        get() = _stateFlow.asStateFlow()

    /**
     * The current navigation state held by the manager.
     */
    public val currentState: NavigationState
        get() = _stateFlow.value

    internal fun updateState(state: NavigationState) {
        _stateFlow.value = state
    }
}

/**
 * Creates an instance of [NavigationManager] using a lambda function to provide the initial state.
 *
 * @param state A lambda function that returns the initial [NavigationState].
 *
 * @return A new instance of [NavigationManager].
 */
@NavigationDslMarker
public fun NavigationManager(state: () -> NavigationState) = NavigationManager(state())

/**
 * Executes a [NavigationCommand], updating the current [NavigationState] accordingly.
 *
 * @param command The navigation command to execute.
 */
public fun NavigationManager.execute(command: NavigationCommand) {
    updateState(command.execute(currentState))
}