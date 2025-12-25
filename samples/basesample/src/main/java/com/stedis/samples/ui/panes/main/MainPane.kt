@file:Suppress("DEPRECATED_ANNOTATION")

package com.stedis.samples.ui.panes.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.stedis.navigation.compose.LocalNavigationHost
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.compose.Scene
import com.stedis.navigation.compose.plus
import com.stedis.navigation.compose.scenes.ListDetailSceneStrategy
import com.stedis.navigation.compose.scenes.TwoPaneSceneStrategy
import com.stedis.navigation.core.inside
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.MoreInfoDestination
import com.stedis.samples.navigation.ext.back
import com.stedis.samples.navigation.ext.forward
import com.stedis.samples.ui.component.NavigationBar

@Composable
fun MainPane() {
    val navigationManager = LocalNavigationManager.current
    val currentNavigationHost = LocalNavigationHost.current

    currentNavigationHost.selectedChild?.let {
        if (it.stack.size > 1) {
            BackHandler {
                navigationManager.back { inside(it.hostName) }
            }
        }
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .weight(1f),
            ) {
                currentNavigationHost.selectedChild?.let {
                    Scene(it, ListDetailSceneStrategy + TwoPaneSceneStrategy)
                }
            }

            NavigationBar(
                isVertical = false,
                currentPage = currentNavigationHost.selectedChild?.hostName ?: "",
                onFriendsClick = { navigationManager.forward(Hosts.FRIENDS.name) },
                onNewsClick = { navigationManager.forward(Hosts.NEWS.name) },
                onMoreInfoClick = { navigationManager.forward(MoreInfoDestination) }
            )
        }
    } else {
        Row {
            NavigationBar(
                isVertical = true,
                currentPage = currentNavigationHost.selectedChild?.hostName ?: "",
                onFriendsClick = { navigationManager.forward(Hosts.FRIENDS.name) },
                onNewsClick = { navigationManager.forward(Hosts.NEWS.name) },
                onMoreInfoClick = { navigationManager.forward(MoreInfoDestination) }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .weight(1f),
            ) {
                currentNavigationHost.selectedChild?.let {
                    Scene(it, ListDetailSceneStrategy + TwoPaneSceneStrategy)
                }
            }
        }
    }
}