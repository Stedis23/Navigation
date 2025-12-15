package com.stedis.navigation.compose.scenes

import android.util.Log
import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.Pane
import com.stedis.navigation.compose.Scene
import com.stedis.navigation.compose.SceneStrategy
import com.stedis.navigation.core.NavigationHost

public object SinglePaneSceneStrategy : SceneStrategy {

    @Composable
    override fun calculateScene(navigationHost: NavigationHost): Scene =
        SinglePaneScene()
}

public class SinglePaneScene() : Scene {

    override val composable: @Composable ((NavigationHost) -> Unit) =
        {
            val paneKey = (it.currentDestination as? ComposeDestination)
                ?.metadata[PaneKey.PANE_KEY] as? String
                ?: PaneKey.DEFAULT_PANE

            Pane(
                navigationHost = it,
                key = paneKey,
            )
        }
}