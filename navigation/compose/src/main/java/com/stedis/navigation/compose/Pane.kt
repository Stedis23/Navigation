package com.stedis.navigation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationHost

val LocalSaveableStateHolder = compositionLocalOf<SaveableStateHolder> {
    error("No SaveableStateHolder provided")
}

val LocalNavigationHost = compositionLocalOf<NavigationHost> {
    error("No NavigationHost provided")
}

val LocalDestination = compositionLocalOf<Destination> {
    error("No Destination provided")
}


/**
 * Composable function that displays the provided Compose destination screen.
 *
 * This function takes a [ComposeDestination] as a parameter and invokes its
 * defined composable function to render the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework,
 * allowing for seamless integration of destination screens within a Compose
 * hierarchy.
 *
 * The function uses a [SaveableStateHolder] to manage the state of the destination
 * screen, ensuring that any state associated with the screen is saved and restored
 * appropriately during configuration changes, such as screen rotations.
 *
 * Example usage:
 * ```
 * Pane(destination = MyComposeDestination())
 * ```
 *
 * Note: Ensure that the [ComposeDestination] provided has been properly configured
 * within the navigation system, and that its composable function is capable of handling
 * the required parameters and state.
 *
 * @param destination The [ComposeDestination] whose composable screen should be displayed.
 * This destination must implement the [ComposeDestination] interface and provide
 * a valid composable function. The composable function will be invoked with the
 * destination as a parameter, allowing it to access any necessary data or state.
 *
 * @param modifier An optional [Modifier] to be applied to the container of the
 * destination screen. This can be used to customize the layout, padding, or other
 * visual properties of the displayed screen.
 */
@Composable
public fun Pane(
    destination: ComposeDestination,
    modifier: Modifier = Modifier,
) {
    val saveableStateHolder = LocalSaveableStateHolder.current

    CompositionLocalProvider(
        LocalDestination provides destination,
    )
    {
        saveableStateHolder.SaveableStateProvider(
            key = destination.toString(),
            content = {
                Box(modifier = modifier) {
                    destination.composable.invoke(destination)
                }
            }
        )
    }
}

/**
 * Composable function that displays the current destination screen from the provided [NavigationHost].
 *
 * This function takes a [NavigationHost] as a parameter and retrieves the current destination
 * to invoke its defined composable function for rendering the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework, allowing for seamless
 * integration of destination screens within a Compose hierarchy.
 *
 * The function utilizes a [SaveableStateHolder] to manage the state of the displayed destination
 * screen, ensuring that any state associated with the screen is saved and restored appropriately
 * during configuration changes, such as screen rotations.
 *
 * Example usage:
 * ```
 * Pane(navigationHost = myNavigationHost)
 * ```
 *
 * Note: Ensure that the provided [NavigationHost] is properly configured and that its current
 * destination implements the [ComposeDestination] interface, providing a valid composable function
 * that can handle any required parameters and state.
 *
 * @param navigationHost The [NavigationHost] that contains the current destination to be displayed.
 * This host must be configured to manage navigation within the application, and its current destination
 * must implement the [ComposeDestination] interface.
 *
 * @param modifier An optional [Modifier] to be applied to the container of the destination screen.
 * This can be used to customize the layout, padding, or other visual properties of the displayed screen.
 */
@Composable
public fun Pane(
    navigationHost: NavigationHost,
    modifier: Modifier = Modifier,
) {
    val saveableStateHolder = LocalSaveableStateHolder.current

    val destination = navigationHost.currentDestination
    CompositionLocalProvider(
        LocalNavigationHost provides navigationHost,
        LocalDestination provides destination,
    )
    {
        saveableStateHolder.SaveableStateProvider(
            key = destination.toString(),
            content = {
                Box(modifier = modifier) {
                    (destination as? ComposeDestination)?.let {
                        it.composable.invoke(it)
                    }
                }
            }
        )
    }
}