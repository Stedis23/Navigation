package com.stedis.navigation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationHost
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.TraversalContext
import com.stedis.navigation.core.getHostsPath
import com.stedis.navigation.core.inside


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
 * Retrieves the specified [NavigationHost] based on the provided hostname.
 *
 * This function utilizes [rememberNavigationState] to access the navigation state
 * and allows for traversal through hosts using an optional context. By default,
 * it retrieves the last host in the path for the specified hostname. If a context
 * is provided, it will be used to determine the appropriate traversal path to
 * the specified host, allowing for more complex navigation scenarios.
 *
 * Example of using rememberNavigationHost:
 * ```kotlin
 * val globalHost = rememberNavigationHost("GLOBAL_HOST") // returns host with name GLOBAL_HOST
 *
 * val specificHost = rememberNavigationHost("SPORT_HOST") {
 *     inside("NEWS_HOST")
 * } // returns the last host in the traversal path that contains the NEWS_HOST node and ends with the SPORT_HOST node
 * ```
 *
 * @param hostName The name of the host to retrieve.
 * @param context An optional lambda function that provides a [TraversalContext].
 *                If provided, it will be used to determine the traversal path
 *                to the specified host.
 * @return The [NavigationHost] that corresponds to the specified hostname.
 */
@Composable
public fun rememberNavigationHost(
    hostName: String,
    context: (TraversalContext.() -> TraversalContext)? = null,
): NavigationHost {
    val state = rememberNavigationState()

    val traversalContext = TraversalContext(
        hosts = state.hosts,
        points = emptyList(),
    )

    return if (context != null) {
        traversalContext
            .context()
            .inside(hostName)
            .getHostsPath()
            .last()
    } else {
        traversalContext
            .inside(hostName)
            .getHostsPath()
            .last()
    }
}

/**
 * Retrieves the current [Destination] from host's [NavigationState].
 * By default, this function returns the current destination associated with the current root host.
 * If a context is provided, it returns the current destination of the last host in the traversal path.
 * This allows for more complex navigation scenarios by specifying which host's destination to retrieve.
 *
 * Example of using rememberCurrentDestination:
 * ```
 * val firstDestination = rememberCurrentDestination()
 *
 * val secondDestination = rememberCurrentDestination {
 *     inside "NEWS_HOST" inside "SPORT_HOST"
 * } // return current destination of host SPORT_HOST
 * ```
 *
 * @param context An optional lambda function that provides a [TraversalContext]. If provided,
 *                it will be used to determine the current destination within the specified context.
 *
 * @return The current [Destination] for the active host or the last host in the provided context.
 */
@Composable
public fun rememberCurrentDestination(context: (TraversalContext.() -> TraversalContext)? = null): Destination {
    val state = rememberNavigationState()

    return if (context != null) {
        val hosts = TraversalContext(
            hosts = state.hosts,
            points = emptyList(),
        )
            .context()
            .getHostsPath()

        val lastHost = hosts.last()

        lastHost.currentDestination
    } else {
        state.currentDestination
    }
}

/**
 * Retrieves the current [Destination] from the currently active root host's [NavigationState].
 * This function utilizes [rememberNavigationState] to access the state and
 * returns the current destination associated with the active host.
 *
 * @return The current [Destination] for the active host.
 */
@Composable
@Deprecated("This method is deprecated. Use rememberCurrentDestination(context: (TraversalContext.() -> TraversalContext)? = null) instead.")
public fun rememberCurrentDestination(): Destination {
    val state = rememberNavigationState()
    return state.currentDestination
}

/**
 * Retrieves the current [Destination] from a specified root host's [NavigationState].
 * This function takes the host name as a parameter, finds the corresponding host
 * in the navigation state, and returns its current destination.
 *
 * @param hostName The name of the root host from which to retrieve the current destination.
 *
 * @return The current [Destination] associated with the specified host.
 *
 * @throws IllegalArgumentException If no host with the provided name exists in the
 * navigation state.
 */
@Composable
@Deprecated("This method is deprecated. Use rememberCurrentDestination(context: (TraversalContext.() -> TraversalContext)? = null) instead.")
public fun rememberCurrentDestination(hostName: String): Destination {
    val state = rememberNavigationState()
    val host = state.hosts.find { it.hostName == hostName }
        ?: throw error("Host with name $hostName don`t exist!")
    return host.currentDestination
}