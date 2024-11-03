package com.stedis.navigation.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    onRootBack: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    BackHandler(
        onBack = {
            if (navigationManager.currentState.currentHost.store.size > 1) {
                navigationManager.execute(BackCommand())
            } else {
                onRootBack()
            }
        },
    )

    CompositionLocalProvider(
        LocalNavigationManager provides navigationManager,
        content,
    )
}