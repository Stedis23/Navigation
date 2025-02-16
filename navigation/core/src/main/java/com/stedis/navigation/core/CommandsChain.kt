package com.stedis.navigation.core

/**
 * Creates a chain of navigation commands based on the provided initial navigation state.
 * This allows for composing new commands that can handle various scenarios for state changes.
 *
 * Example of using [CommandsChain]:
 * ```
 * object SampleCommand : NavigationCommand {
 *
 *      override fun execute(navigationState: NavigationState): NavigationState =
 *          CommandsChain(navigationState) {
 *              ReplaceCommand(FirstSampleScreen()) then
 *                  ForwardCommand(SecondSampleScreen())
 *          }
 *      }
 * ```
 *
 * @param state The initial navigation state to start the command chain.
 * @param body A lambda function that defines the sequence of commands to be executed.
 *
 * @return A new [NavigationState] resulting from the execution of the command chain.
 */
public fun CommandsChain(state: NavigationState, body: CommandsChainBuilder.() -> NavigationCommand): NavigationState =
    CommandsChainBuilder(state).also { it.lastCommand = it.body() }.build()

/**
 * A builder class for creating a chain of navigation commands.
 *
 * @property state The initial [NavigationState] for the command chain.
 */
class CommandsChainBuilder(state: NavigationState) {

    /**
     * The current navigation state being modified by the command chain.
     */
    public var currentState: NavigationState = state
        private set

    /**
     * The last command in the chain that will be executed.
     */
    public var lastCommand: NavigationCommand? = null

    /**
     * Chains two navigation commands together, executing the first command
     * and updating the current state with the result before returning the second command.
     *
     * @param other The next navigation command to execute after the current one.
     *
     * @return The next [NavigationCommand] in the chain.
     */
    public infix fun NavigationCommand.then(other: NavigationCommand): NavigationCommand {
        currentState = this.execute(currentState)
        return other
    }

    /**
     * Builds and executes the [CommandsChain], returning the final [NavigationState].
     *
     * @return The final [NavigationState] after executing the command chain.
     *
     * @throws Error if the command chain is empty.
     */
    public fun build(): NavigationState =
        lastCommand?.execute(currentState) ?: throw error("command chain cannot be empty")
}