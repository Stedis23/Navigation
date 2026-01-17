package com.stedis.navigation.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
 * [NavigationManager] manages the navigation state within the application, allowing for updates and modifications
 * through [NavigationCommand].
 *
 * @property initialState The initial [NavigationState] to set for the manager.
 */
public class NavigationManager(
    private val initialState: NavigationState,
    internal val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)
) {

    internal val commandsQueue = CommandsQueue()

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

    init {
        coroutineScope.launch {
            for (command in commandsQueue.getCommands()) {
                updateState(command.execute(currentState))
            }
        }
    }

    private fun updateState(state: NavigationState) {
        _stateFlow.value = state
    }

    /**
     * Terminals the [NavigationManager] by cancelling its coroutine scope and closing the command queue.
     *
     * Once this method is called:
     * 1. Any ongoing or pending navigation commands in the [commandsQueue] will be discarded.
     * 2. The [coroutineScope] will be cancelled, stopping the command processor loop.
     * 3. No further commands can be executed via [execute].
     *
     * This should be called when the manager is no longer needed (e.g., when the associated
     * Lifecycle owner is destroyed) to prevent memory leaks and background processing
     * of stale navigation events.
     */
    public fun cancel() {
        coroutineScope.cancel()
        commandsQueue.close()
    }
}

/**
 * Schedules a [NavigationCommand] for execution by adding it to the internal command queue.
 *
 * This method ensures that all navigation updates are processed sequentially in a thread-safe
 * manner. The command is enqueued within the [NavigationManager]'s [coroutineScope] and will
 * be executed as soon as the processor reaches it in the queue.
 *
 * Once processed, the command will update the [currentState] of this manager.
 *
 * @param command The navigation command to be queued and executed.
 * * @see NavigationCommand
 * @see NavigationState
 */
public fun NavigationManager.execute(command: NavigationCommand) {
    coroutineScope.launch { commandsQueue.enqueue(command) }
}