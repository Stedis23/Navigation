package com.stedis.navigation.core

/**
 * Interface representing a navigation command that can modify the [NavigationState].
 *
 * Implementations of this interface define specific actions that can be taken
 * to change the current navigation state within the application.
 */
interface NavigationCommand {

    /**
     * Executes the navigation command, returning a new instance of [NavigationState].
     *
     * @param navigationState The current navigation state to modify.
     *
     * @return A new [NavigationState] reflecting the changes made by this command.
     */
    public fun execute(navigationState: NavigationState): NavigationState
}

/**
 * Creates a [NavigationCommand] that modifies the navigation state using the provided parameters.
 *
 * This command utilizes a [NavigationStateBuilder] to construct a new navigation state.
 *
 * This can be useful if you need some special case of navigation command.
 * For creating template commands it is better to implement the interface of [com.stedis.navigation.core.NavigationCommand]
 * You can see examples of this [here](https://github.com/Stedis23/Navigation/blob/d90d91c85b177ffa2e2256c5860aa8f0c76f5a91/navigation/core/src/main/java/com/stedis/navigation/core/NavigationCommands.kt)
 *
 * Example of using [NavigationCommand]:
 * ```
 * navigationManager.execute(
 *     NavigationCommand {
 *         setCurrentHost("NEWS")
 *         removeHost("FRIENDS")
 *     }
 * )
 * ```
 *
 * @param params A lambda function that defines the modifications to be applied to the [NavigationStateBuilder].
 *
 * @return A new instance of [NavigationCommand] that can be executed to modify the navigation state.
 */
@NavigationDslMarker
public fun NavigationCommand(params: NavigationStateBuilder.() -> Unit) =
    object : NavigationCommand {

        override fun execute(navigationState: NavigationState): NavigationState =
            navigationState.buildNewState(params)
    }

/**
 * Creates a [NavigationCommand] that modifies the current navigation state with modify the current root host using the provided
 *
 * This command utilizes a [NavigationHostBuilder] to construct a new navigation state that includes modifications
 * to the current host.
 *
 * This can be useful if you need some special case of navigation command.
 * For creating template commands it is better to implement the interface of [com.stedis.navigation.core.NavigationCommand]
 * You can see examples of this [here](https://github.com/Stedis23/Navigation/blob/d90d91c85b177ffa2e2256c5860aa8f0c76f5a91/navigation/core/src/main/java/com/stedis/navigation/core/NavigationCommands.kt)
 *
 * Example of using [CurrentHostNavigationCommand]:
 * ```
 * navigationManager.execute(
 *     CurrentHostNavigationCommand {
 *         popDestination()
 *         replaceDestination(FriendsFeedDestination)
 *         addDestination(FriendInfoDestination(friend.Id))
 *     }
 * )
 * ```
 *
 * @param params A lambda function that defines the modifications to be applied to the [NavigationHostBuilder].
 *
 * @return A new instance of [NavigationCommand] that can be executed to build the navigation state with modify the current host.
 */
@Suppress("FunctionName")
@NavigationDslMarker
public fun CurrentHostNavigationCommand(params: NavigationHostBuilder.() -> Unit) =
    object : NavigationCommand {

        override fun execute(navigationState: NavigationState): NavigationState =
            navigationState.buildNewStateWithCurrentHost(params)
    }