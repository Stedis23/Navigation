package com.stedis.navigation.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationHost
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.execute

/**
 * The primary entry point for managing navigation within the application.
 * This composable function orchestrates the entire navigation lifecycle,
 * providing essential services such as handling back navigation,
 * managing ViewModel instances, and coordinating the navigation state.
 *
 * This function accepts a [NavigationManager] to handle navigation commands
 * and a default [ViewModelFactory] for creating ViewModel instances associated
 * with navigation destinations. It also offers a mechanism for customizing
 * back navigation behavior and allows for the specification of a custom action
 * when the root back action is triggered.
 *
 * Key Responsibilities:
 * - **Lifecycle Management**: Effectively manages the lifecycle of navigation states, ensuring
 *   that ViewModels are instantiated and disposed of appropriately based on the navigation stack.
 *   Use [NavigationViewModel] to allow navigation to follow the lifecycle of the viewmodel.
 * - **Default ViewModel Factory**: Supplies a default factory for creating ViewModels, ensuring
 *   that each destination has access to its required dependencies seamlessly.
 * - **Back Navigation Handling**: Implements back navigation logic, enabling users to navigate
 *   back through the stack of destinations. If the current host contains multiple destinations,
 *   it executes the back command; otherwise, it triggers the provided [onRootBack] action.
 * - **State Management**: Maintains the state of destinations, ensuring that when navigating back
 *   to a previous destination, any saved state is restored unless the destination is marked with
 *   the [NoSaveState] annotation.
 * - **Composition Local Providers**: Provides access to the navigation ViewModel, ViewModel factory,
 *   and navigation manager throughout the composition tree, allowing child composables to access
 *   these services effortlessly.
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
 *     Pane(rememberNavigationHost("Main"))
 * }
 * ```
 *
 * @param navigationManager The [NavigationManager] responsible for managing
 * the navigation state and executing navigation commands.
 * @param defaultViewModelFactory The [ViewModelProvider.Factory] used to create
 * ViewModels for destinations. Defaults to [DefaultViewModelFactory].
 * @param backHandlerEnabled A boolean flag indicating whether the back
 * handler should be enabled. Defaults to true.
 * @param onRootBack A lambda function to be executed when the back action
 * is triggered at the root of the navigation stack. This allows for custom
 * behavior when the user attempts to navigate back from the root.
 * @param content A composable function representing the content to be
 * displayed for the current destination. This content will be rendered
 * based on the current navigation state.
 *
 * @throws IllegalStateException If no [ViewModelStoreOwner] is provided
 * via [LocalViewModelStoreOwner].
 *
 * This function is marked with `@NonRestartableComposable` to indicate that
 * it should not be restarted during recompositions, ensuring consistent
 * navigation behavior throughout the app.
 */
@Composable
@NonRestartableComposable
public fun Navigation(
    navigationManager: NavigationManager,
    defaultViewModelFactory: ViewModelProvider.Factory = DefaultViewModelFactory(),
    backHandlerEnabled: Boolean = true,
    onRootBack: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val mainNavigationViewModel =
        ViewModelProvider(viewModelStoreOwner)[MainNavigationViewModel::class.java]

    BackHandler(
        enabled = backHandlerEnabled,
        onBack = {
            if (navigationManager.currentState.currentHost.stack.size > 1) {
                navigationManager.execute(BackCommand())
            } else {
                onRootBack()
            }
        },
    )

    val saveableStateHolder = rememberSaveableStateHolder()

    CompositionLocalProvider(
        LocalMainNavigationViewModel provides mainNavigationViewModel,
        LocalViewModelFactory provides defaultViewModelFactory,
        LocalNavigationManager provides navigationManager,
        LocalSaveableStateHolder provides saveableStateHolder,
    )
    {

        val savedViewModelKeys = rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }
        val savedViewModelLongKeys = rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }
        val savedStateHolderKeys = rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }

        val state = navigationManager.stateFlow.collectAsState()
        LaunchedEffect(key1 = state.value) {
            clearUnavailableViewModelKeys(
                state.value.hosts,
                mainNavigationViewModel,
                savedViewModelKeys,
                savedViewModelLongKeys,
            )

            clearUnavailableStateHolderKeys(
                state.value.hosts,
                savedStateHolderKeys,
                saveableStateHolder,
            )
        }

        content()
    }
}

private fun clearUnavailableViewModelKeys(
    hosts: List<NavigationHost>,
    navigationViewModel: MainNavigationViewModel,
    savedViewModelKeys: MutableState<Set<String>>,
    savedViewModelLongKeys: MutableState<Set<String>>,
) {
    val viewModelKeys = hosts.flatMap { host ->
        collectViewModelKeys(host, navigationViewModel)
    }.toSet()

    val viewModelLongKeys = hosts.flatMap { host ->
        collectViewModelLongKeys(host, navigationViewModel)
    }.toSet()

    val unavailableViewModelKeys =
        savedViewModelKeys.value.filterNot { it in viewModelKeys }

    val unavailableViewModelLongKeys =
        savedViewModelLongKeys.value.filterNot { it in viewModelLongKeys }
    (unavailableViewModelKeys + unavailableViewModelLongKeys).forEach(navigationViewModel::remote)

    savedViewModelKeys.value = viewModelKeys
    savedViewModelLongKeys.value = viewModelLongKeys
}

private fun clearUnavailableStateHolderKeys(
    hosts: List<NavigationHost>,
    savedStateHolderKeys: MutableState<Set<String>>,
    saveableStateHolder: SaveableStateHolder,
) {
    val stateHolderKeys = hosts.flatMap { host ->
        collectKeys(host)
    }.toSet()

    val noSaveStateKeys = hosts.flatMap { host ->
        collectNoSaveStateKeys(host)
    }


    val unavailableKeys = savedStateHolderKeys.value.filterNot { it in stateHolderKeys }
    (unavailableKeys + noSaveStateKeys).forEach(saveableStateHolder::removeState)
    savedStateHolderKeys.value = stateHolderKeys
}

private fun collectViewModelKeys(
    host: NavigationHost,
    navigationViewModel: MainNavigationViewModel,
): Set<String> {
    val keys = host.stack.flatMap { destination ->
        val viewModelStoreOwnersKeys = navigationViewModel.getKeys()
        val keys = viewModelStoreOwnersKeys.filter { it.contains(destination.toString()) }
        keys.mapNotNull {
            val status = navigationViewModel.getViewModelStoreOwnerStatus(it)
            if (status != null && status != false) {
                it
            } else {
                null
            }
        }
    }.toMutableSet()
    host.children.forEach { child ->
        keys.addAll(collectViewModelKeys(child, navigationViewModel))
    }

    return keys
}

private fun collectViewModelLongKeys(
    host: NavigationHost,
    navigationViewModel: MainNavigationViewModel,
): Set<String> {
    val keys = host.stack.flatMap { destination ->
        val viewModelStoreOwnersKeys = navigationViewModel.getKeys()
        val keys = viewModelStoreOwnersKeys.filter { it.contains(destination.toString()) }
        keys
    }.toMutableSet()
    host.children.forEach { child ->
        keys.addAll(collectViewModelKeys(child, navigationViewModel))
    }

    return keys
}

private fun collectKeys(host: NavigationHost): Set<String> {
    val keys = host.stack.map { destination -> destination.toString() }.toMutableSet()
    host.children.forEach { child ->
        keys.addAll(collectKeys(child))
    }

    return keys
}

private fun collectNoSaveStateKeys(host: NavigationHost): Set<String> {
    val keys = host.stack.mapNotNull { destination ->
        if (isNoSaveStateDestination(destination) && host.stack.last() != destination) {
            destination.toString()
        } else null
    }.toMutableSet()

    host.children.forEach { child ->
        keys.addAll(collectNoSaveStateKeys(child))
    }

    return keys
}

private fun isNoSaveStateDestination(destination: Destination): Boolean =
    destination::class.annotations.filterIsInstance<NoSaveState>().isNotEmpty()