package com.stedis.navigation.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.execute

public val LocalNavigationManager: ProvidableCompositionLocal<NavigationManager> =
    compositionLocalOf { error("LocalNavigationManager must be called inside Navigation()") }

@Composable
public fun rememberNavigationState(): NavigationState {
    val navigationManager = LocalNavigationManager.current
    val state = navigationManager.stateFlow.collectAsState()
    val remState by remember { state }
    return remState
}

@Composable
public fun rememberCurrentDestination(): Destination {
    val state = rememberNavigationState()
    return state.currentDestination
}

@Composable
@NonRestartableComposable
public fun Navigation(
    navigationManager: NavigationManager,
    viewModelFactory: ViewModelFactory = DefaultViewModelFactory(),
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
        LocalViewModelFactory provides viewModelFactory,
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