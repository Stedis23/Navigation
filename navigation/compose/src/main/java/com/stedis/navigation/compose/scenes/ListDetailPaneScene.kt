package com.stedis.navigation.compose.scenes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.NavigationAnimations
import com.stedis.navigation.compose.Pane
import com.stedis.navigation.compose.Scene
import com.stedis.navigation.compose.SceneStrategy
import com.stedis.navigation.core.NavigationHost

public object ListDetailSceneStrategy : SceneStrategy {

    @Composable
    override fun calculateScene(navigationHost: NavigationHost): Scene? {
        val lastTwoDestinations =
            navigationHost.stack.takeLast(2) as? List<ComposeDestination> ?: return null

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        return when {
            hasListAndDetailPane(lastTwoDestinations) -> ListDetailScene()
            hasOnlyListPane(lastTwoDestinations) -> ListDetailScene()
            else -> null
        }
    }

    private fun hasListAndDetailPane(destinations: List<ComposeDestination>): Boolean {
        return destinations.first().metadata.containsKey(ListDetailScene.LIST_PANE_KEY) &&
                destinations.last().metadata.containsKey(ListDetailScene.DETAIL_PANE_KEY)
    }

    private fun hasOnlyListPane(destinations: List<ComposeDestination>): Boolean {
        return destinations.last().metadata.containsKey(ListDetailScene.LIST_PANE_KEY)
    }
}

public class ListDetailScene : Scene {
    override val composable: @Composable ((NavigationHost) -> Unit) = { host ->
        val lastTwoDestinations = host.stack.takeLast(2) as? List<ComposeDestination>
            ?: throw error("all destinations must be ComposeDestination")

        val hasDetailPane = lastTwoDestinations.last().metadata.containsKey(DETAIL_PANE_KEY)
        val onlyListPane = !hasDetailPane

        val listDestination = if (onlyListPane) {
            lastTwoDestinations.last()
        } else {
            lastTwoDestinations.first()
        }

        val (listPaneWeight, detailPaneWeight) = calculatePaneWeights(
            lastTwoDestinations,
            onlyListPane
        )

        val listStack = if (onlyListPane) {
            host.stack
        } else {
            host.stack.dropLast(1)
        }
        val listHost = host.copy(
            stack = listStack,
            currentDestination = listDestination
        )

        Row(Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(listPaneWeight)) {
                Pane(
                    navigationHost = listHost,
                    key = LIST_PANE_KEY,
                )
            }

            Box(modifier = Modifier.weight(detailPaneWeight)) {
                if (onlyListPane) {
                    val metadata =
                        listDestination.metadata[LIST_PANE_KEY] as ListPaneMetaData
                    metadata.placeholder()
                } else {
                    Pane(
                        navigationHost = host,
                        key = DETAIL_PANE_KEY,
                        navigationAnimations = NavigationAnimations(
                            replaceAnimation = fadeIn().togetherWith(fadeOut()),
                        )
                    )
                }
            }
        }
    }

    private fun calculatePaneWeights(
        destinations: List<ComposeDestination>,
        onlyListPane: Boolean
    ): Pair<Float, Float> {
        val metadata = if (onlyListPane) {
            destinations.last().metadata[LIST_PANE_KEY] as ListPaneMetaData
        } else {
            destinations.first().metadata[LIST_PANE_KEY] as ListPaneMetaData
        }
        val weight = metadata.weight
        return weight to (1.0f - weight)
    }

    public companion object {
        internal const val LIST_PANE_KEY = "ListPane"
        internal const val DETAIL_PANE_KEY = "DetailPane"

        public fun listPane(
            placeholder: @Composable (() -> Unit) = {},
            weight: Float = 0.5f,
        ): Map<Any, Any> = mapOf(LIST_PANE_KEY to ListPaneMetaData(placeholder, weight))

        public fun detailPane(): Map<Any, Any> = mapOf(DETAIL_PANE_KEY to true)
    }
}

data class ListPaneMetaData(
    val placeholder: @Composable (() -> Unit),
    val weight: Float,
)
