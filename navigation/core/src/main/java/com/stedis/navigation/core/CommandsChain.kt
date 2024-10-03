package com.stedis.navigation.core

public fun CommandsChain(state: NavigationState, body: CommandsChainBuilder.() -> NavigationCommand): NavigationState =
    CommandsChainBuilder(state).also { it.lastCommand = it.body() }.build()

class CommandsChainBuilder(state: NavigationState) {

    public var currentState: NavigationState = state
        private set

    public var lastCommand: NavigationCommand? = null

    public infix fun NavigationCommand.then(other: NavigationCommand): NavigationCommand {
        currentState = this.execute(currentState)
        return other
    }

    public fun build(): NavigationState =
        lastCommand?.execute(currentState) ?: throw error("command chain cannot be empty")
}

