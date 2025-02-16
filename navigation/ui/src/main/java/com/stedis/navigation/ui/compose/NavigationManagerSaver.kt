package com.stedis.navigation.ui.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.restoreState
import com.stedis.navigation.core.saveState

/**
 * Composable function that creates and remembers an instance of [NavigationManager]
 * based on the provided [NavigationState]. This function ensures that the navigation
 * manager is preserved across recompositions and configuration changes, making it
 * suitable for use in a Jetpack Compose application.
 *
 * Example usage:
 * ```
 * val navigationManager = rememberNavigationManager(currentNavigationState)
 * ```
 *
 * @param navigationState The current [NavigationState] that the [NavigationManager] will use.
 *
 * @return A remembered instance of [NavigationManager] that maintains its state.
 */
@Composable
public fun rememberNavigationManager(navigationState: NavigationState): NavigationManager {
    return rememberSaveable(saver = NavigationManagerSaver()) {
        NavigationManager { navigationState }
    }
}

/**
 * Provides a [Saver] implementation for [NavigationManager], allowing it to be saved
 * and restored using a [Bundle]. This is essential for preserving the state of the
 * navigation manager across configuration changes, such as screen rotations.
 *
 * @return A [Saver] that can save and restore the state of a [NavigationManager].
 */
private fun NavigationManagerSaver(): Saver<NavigationManager, *> =
    Saver<NavigationManager, Bundle>(
        save = { it.saveState() },
        restore = { NavigationManager { restoreState(it) } }
    )