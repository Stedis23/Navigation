package com.stedis.samples.panes.root

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.compose.Navigation
import com.stedis.navigation.compose.Pane
import com.stedis.navigation.compose.rememberCurrentDestination
import com.stedis.navigation.compose.rememberNavigationManager
import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.NavigationHost
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.execute
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.WebPageDestination
import com.stedis.samples.panes.welcome.WelcomeDestination

@Composable
fun RootPane(
    onOpenWebPage: (String) -> Unit = {},
    onRootBack: () -> Unit = {}
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

        when (val destination = rememberCurrentDestination(Hosts.GLOBAL.name)) {
            is WebPageDestination -> {
                onOpenWebPage(destination.url)
                navigationManager.execute(BackCommand)
            }

            else -> {
                Pane(destination as ComposeDestination)
            }
        }

    }
}