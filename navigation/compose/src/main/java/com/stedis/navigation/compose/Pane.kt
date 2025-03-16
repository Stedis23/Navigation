package com.stedis.navigation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Composable function that displays the provided Compose destination screen.
 *
 * This function takes a [ComposeDestination] as a parameter and invokes its
 * defined composable function to render the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework,
 * allowing for seamless integration of destination screens within a Compose
 * hierarchy.
 *
 * @param destination The [ComposeDestination] whose composable screen should be displayed.
 * This destination must implement the [ComposeDestination] interface and provide
 * a valid composable function.
 */
@Composable
public fun Pane(
    destination: ComposeDestination,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        destination.composable.invoke(destination)
    }
}