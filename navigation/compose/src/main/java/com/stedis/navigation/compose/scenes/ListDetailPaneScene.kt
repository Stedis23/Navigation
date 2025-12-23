package com.stedis.navigation.compose.scenes

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.stedis.navigation.core.Destination
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
        return destinations.first().metadata.containsKey(PaneKey.LIST_PANE_KEY) &&
                destinations.last().metadata.containsKey(PaneKey.DETAIL_PANE_KEY)
    }

    private fun hasOnlyListPane(destinations: List<ComposeDestination>): Boolean {
        return destinations.last().metadata.containsKey(PaneKey.LIST_PANE_KEY)
    }
}

public class ListDetailScene : Scene {
    override val composable: @Composable ((NavigationHost) -> Unit) = { host ->
        val layoutState = computeLayoutState(host)
        val listPaneMetadata =
            layoutState.listDestination.metadata[PaneKey.LIST_PANE_KEY] as? ListPaneMetaData
                ?: throw IllegalStateException("All destinations must have ListPaneMetaData")

        val listHost = host.copy(
            stack = layoutState.listStack,
            currentDestination = layoutState.listDestination
        )

        when {
            layoutState.onlyListPane -> {
                renderListOnly(
                    listHost = listHost,
                    listPaneMetadata = listPaneMetadata
                )
            }

            else -> {
                renderListAndDetail(
                    listHost = listHost,
                    detailHost = host,
                    listPaneMetadata = listPaneMetadata,
                )
            }
        }
    }

    public companion object {
        public fun listPane(
            placeholder: @Composable (() -> Unit)? = null,
            weight: Float = 0.5f,
        ): Map<Any, Any> =
            mapOf(
                PaneKey.LIST_PANE_KEY to ListPaneMetaData(placeholder, weight),
                PaneKey.PANE_KEY to PaneKey.LIST_PANE_KEY,
            )

        public fun detailPane(): Map<Any, Any> = mapOf(
            PaneKey.DETAIL_PANE_KEY to true,
            PaneKey.PANE_KEY to PaneKey.DETAIL_PANE_KEY,
        )
    }
}

private data class LayoutState(
    val onlyListPane: Boolean,
    val listDestination: ComposeDestination,
    val detailDestination: ComposeDestination?,
    val listStack: List<Destination>
)

private fun computeLayoutState(host: NavigationHost): LayoutState {
    val lastTwoDestinations = host.stack.takeLast(2) as? List<ComposeDestination>
        ?: throw IllegalStateException("All destinations must be ComposeDestination and stack must have at least one element")

    val hasDetailPane = lastTwoDestinations.last().metadata.containsKey(PaneKey.DETAIL_PANE_KEY)
    val onlyListPane = !hasDetailPane

    val listDestination = if (onlyListPane) {
        lastTwoDestinations.last()
    } else {
        lastTwoDestinations.first()
    }

    val detailDestination = if (onlyListPane) null else lastTwoDestinations.last()

    val listStack = if (onlyListPane) {
        host.stack
    } else {
        host.stack.dropLast(1)
    }

    return LayoutState(
        onlyListPane = onlyListPane,
        listDestination = listDestination,
        detailDestination = detailDestination,
        listStack = listStack
    )
}

@Composable
private fun renderListOnly(
    listHost: NavigationHost,
    listPaneMetadata: ListPaneMetaData
) {
    if (listPaneMetadata.placeholder != null) {
        val placeholderWeight = 1.0f - listPaneMetadata.weight
        Row(Modifier.fillMaxSize()) {
            PaneBox(modifier = Modifier.weight(listPaneMetadata.weight)) {
                Pane(navigationHost = listHost)
            }
            PaneBox(modifier = Modifier.weight(placeholderWeight)) {
                listPaneMetadata.placeholder.invoke()
            }
        }
    } else {
        PaneBox(Modifier.fillMaxSize()) {
            Pane(navigationHost = listHost)
        }
    }
}

@Composable
private fun renderListAndDetail(
    listHost: NavigationHost,
    detailHost: NavigationHost,
    listPaneMetadata: ListPaneMetaData,
) {
    val detailPaneWeight = 1.0f - listPaneMetadata.weight
    Row(Modifier.fillMaxSize()) {
        PaneBox(modifier = Modifier.weight(listPaneMetadata.weight)) {
            Pane(navigationHost = listHost)
        }
        PaneBox(modifier = Modifier.weight(detailPaneWeight)) {
            Pane(
                navigationHost = detailHost,
                navigationAnimations = NavigationAnimations(
                    replaceAnimation = fadeIn().togetherWith(fadeOut())
                )
            )
        }
    }
}

@Composable
private fun PaneBox(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
    }
}

data class ListPaneMetaData(
    val placeholder: @Composable (() -> Unit)? = null,
    val weight: Float,
)

