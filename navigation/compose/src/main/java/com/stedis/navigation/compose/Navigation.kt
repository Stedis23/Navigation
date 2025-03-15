package com.stedis.navigation.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.execute

/**
 * The primary entry point for managing navigation within the application.
 * This composable function orchestrates the entire navigation lifecycle,
 * providing essential services such as handling the back navigation,
 * managing ViewModel instances, and coordinating the navigation state.
 *
 * This function accepts a [NavigationManager] to handle navigation commands
 * and a default [ViewModelFactory] for creating ViewModel instances associated
 * with the navigation destinations. It also provides a mechanism to handle
 * back navigation behavior and allows for customization of the root back action.
 *
 * Key Responsibilities:
 * - **Lifecycle Management**: Manages the lifecycle of navigation states and
 *   ensures that ViewModels are created and destroyed appropriately based on
 *   the navigation stack.
 * - **Default ViewModel Factory**: Provides a default factory for creating
 *   ViewModels, ensuring that each destination has access to its required
 *   dependencies.
 * - **Back Navigation Handling**: Implements back navigation logic, allowing
 *   the user to navigate back through the stack of destinations. If the current
 *   host has more than one destination in its store, it executes the back command;
 *   otherwise, it triggers the provided [onRootBack] action.
 * - **State Management**: Maintains the state of destinations, ensuring that
 *   when navigating back to a destination, any previously saved state is
 *   appropriately restored unless the destination is marked with the
 *   [NoSaveState] annotation.
 * - **Composition Local Providers**: Provides access to the navigation
 *   ViewModel, ViewModel factory, and navigation manager throughout the
 *   composition tree, enabling child composables to access these services
 *   seamlessly.
 *
 * Example Usage:
 * ```
 * Navigation(
 *     navigationManager = rememberNavigationManager(
 *         NavigationState(
 *             NavigationHost(
 *                 hostName = Hosts.ROOT,
 *                 initialDestination = SampleDestination
 *             )
 *         )
 *     )
 * ) {
 *     ComposeScreen(rememberCurrentDestination() as ComposeDestination)
 * }
 * ```
 *
 * @param navigationManager The [NavigationManager] responsible for managing
 * the navigation state and executing navigation commands.
 * @param defaultViewModelFactory The [ViewModelFactory] used to create
 * ViewModels for destinations. Defaults to [DefaultViewModelFactory].
 * @param backHandlerEnabled A boolean flag indicating whether the back
 * handler should be enabled. Defaults to true.
 * @param onRootBack A lambda function to be executed when the back action
 * is triggered at the root of the navigation stack.
 * @param content A composable function representing the content to be
 * displayed for the current destination.
 *
 * @throws IllegalStateException If no [ViewModelStoreOwner] is provided
 * via [LocalViewModelStoreOwner].
 *
 * This function is marked with `@NonRestartableComposable` to indicate that
 * it should not be restarted during recompositions, ensuring consistent
 * navigation behavior.
 */
@Composable  
@NonRestartableComposable
public fun Navigation(
    navigationManager: NavigationManager,
    defaultViewModelFactory: ViewModelFactory = DefaultViewModelFactory(),
    backHandlerEnabled: Boolean = true,
    onRootBack: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val navigationViewModel =
        ViewModelProvider(viewModelStoreOwner)[NavigationViewModel::class.java]

    BackHandler(
        enabled = backHandlerEnabled,
        onBack = {
            if (navigationManager.currentState.currentHost.store.size > 1) {
                navigationManager.execute(BackCommand)
            } else {
                onRootBack()
            }
        },
    )

    CompositionLocalProvider(
        LocalNavigationViewModel provides navigationViewModel,
        LocalViewModelFactory provides defaultViewModelFactory,
        LocalNavigationManager provides navigationManager,
    )
    {
        val saveableStateHolder = rememberSaveableStateHolder()
        val savedKeys = rememberSaveable { mutableStateOf<List<String>>(emptyList()) }

        LaunchedEffect(navigationManager.currentState) {
            val keys = navigationManager.currentState.hosts.flatMap { host ->
                host.store.map { destination -> destination.toString() }
            }

            val noSaveStateKeys = navigationManager.currentState.hosts.flatMap { host ->
                host.store.mapNotNull { destination ->
                    if (isNoSaveStateDestination(destination) && host.store.last() != destination) {
                        destination.toString()
                    } else null
                }
            }

            val unavailableKeys = savedKeys.value.filterNot { it in keys }
            unavailableKeys.forEach(navigationViewModel::remote)
            (unavailableKeys + noSaveStateKeys).forEach(saveableStateHolder::removeState)
            savedKeys.value = keys
        }

        saveableStateHolder.SaveableStateProvider(
            key = rememberCurrentDestination().toString(),
            content = content
        )
    }
}

private fun isNoSaveStateDestination(destination: Destination): Boolean =
    destination::class.annotations.filterIsInstance<NoSaveState>().isNotEmpty()