package com.stedis.navigation.ui.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.restoreState
import com.stedis.navigation.core.saveState

@Composable
public fun rememberNavigationManager(navigationState: NavigationState): NavigationManager {
    return rememberSaveable(saver = NavigationManagerSaver()) {
        NavigationManager { navigationState }
    }
}

private fun NavigationManagerSaver(): Saver<NavigationManager, *> =
    Saver<NavigationManager, Bundle>(
        save = { it.saveState() },
        restore = { NavigationManager { restoreState(it) } }
    )