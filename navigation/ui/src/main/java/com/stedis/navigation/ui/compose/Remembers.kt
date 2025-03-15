package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.NavigationState


/**
 * A composition local that provides access to the [NavigationManager].
 * This local must be accessed within the context of the [Navigation] composable.
 *
 * @throws IllegalStateException If accessed outside the [Navigation] composable.
 */
public val LocalNavigationManager: ProvidableCompositionLocal<NavigationManager> =
    compositionLocalOf { error("LocalNavigationManager must be called inside Navigation()") }

/**
 * Creates and remembers the current [NavigationState] from the [NavigationManager].
 * This function collects the state flow from the [NavigationManager] and returns
 * the current navigation state.
 *
 * @return The current [NavigationState] associated with the [NavigationManager].
 */
@Composable
public fun rememberNavigationState(): NavigationState {
    val navigationManager = LocalNavigationManager.current
    val state = navigationManager.stateFlow.collectAsState()
    val remState by remember { state }
    return remState
}

/**
 * Retrieves the current [Destination] from the currently active host's [NavigationState].
 * This function utilizes [rememberNavigationState] to access the state and
 * returns the current destination associated with the active host.
 *
 * @return The current [Destination] for the active host.
 */
@Composable
public fun rememberCurrentDestination(): Destination {
    val state = rememberNavigationState()
    return state.currentDestination
}

/**
 * Retrieves the current [Destination] from a specified host's [NavigationState].
 * This function takes the host name as a parameter, finds the corresponding host
 * in the navigation state, and returns its current destination.
 *
 * @param hostName The name of the host from which to retrieve the current destination.
 *
 * @return The current [Destination] associated with the specified host.
 *
 * @throws IllegalArgumentException If no host with the provided name exists in the
 * navigation state.
 */
@Composable
public  fun rememberCurrentDestination(hostName: String): Destination {
    val state = rememberNavigationState()
    val host = state.hosts.find { it.hostName == hostName } ?: throw error("Host with name $hostName don`t exist!")
    return host.currentDestination
}