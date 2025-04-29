package com.stedis.navigation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier

val LocalSaveableStateHolder = compositionLocalOf<SaveableStateHolder> {
    error("No SaveableStateHolder provided")
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
    saveableStateHolder.SaveableStateProvider(
        key = destination.toString(),
        content = {
            Box(modifier = modifier) {
                destination.composable.invoke(destination)
            }
        }
    )
}