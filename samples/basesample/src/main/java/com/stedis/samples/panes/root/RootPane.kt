package com.stedis.samples.panes.root

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.compose.Navigation
import com.stedis.navigation.compose.Pane
import com.stedis.navigation.compose.rememberNavigationManager
import com.stedis.navigation.compose.rememberNavigationState
import com.stedis.navigation.core.NavigationHost
import com.stedis.navigation.core.NavigationState
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.WebPageDestination
import com.stedis.samples.navigation.ext.back
import com.stedis.samples.panes.welcome.WelcomeDestination

@Composable
fun RootPane(
    onOpenWebPage: (String) -> Unit = {},
    onRootBack: () -> Unit = {},
) {
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
        val navigationManager = LocalNavigationManager.current
        val state = rememberNavigationState()

        when (val destination = state.currentDestination) {
            is WebPageDestination -> {
                onOpenWebPage(destination.url)
                navigationManager.back()
            }

            else -> Pane(state.currentHost)
        }
    }
}