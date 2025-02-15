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