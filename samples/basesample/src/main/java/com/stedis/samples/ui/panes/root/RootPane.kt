package com.stedis.samples.ui.panes.root

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.Navigation
import com.stedis.navigation.compose.Pane
import com.stedis.navigation.compose.rememberNavigationManager
import com.stedis.navigation.compose.rememberNavigationState
import com.stedis.navigation.core.NavigationHost
import com.stedis.navigation.core.NavigationState
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.WelcomeDestination

@Composable
fun RootPane(onRootBack: () -> Unit = {}) {
    Navigation(
        navigationManager = rememberNavigationManager(
            NavigationState(
                NavigationHost(
                    hostName = Hosts.GLOBAL.name,
                    initialDestination = WelcomeDestination,
                )
            )
        ),
        onRootBack = onRootBack
    ) {
        val state = rememberNavigationState()
        Pane(state.currentHost)
    }
}