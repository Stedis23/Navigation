package com.stedis.navigation.compose.scenes

import androidx.compose.runtime.Composable
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
            Pane(
                navigationHost = it,
                key = PaneKey.CURRENT_PANE,
            )
        }
}