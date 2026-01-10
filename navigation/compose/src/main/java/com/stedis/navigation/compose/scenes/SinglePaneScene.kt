package com.stedis.navigation.compose.scenes

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.Scene
import com.stedis.navigation.compose.SceneStrategy
import com.stedis.navigation.core.NavigationHost

/**
 * A [SceneStrategy] that always creates a [SinglePaneScene] for the current destination.
 *
 * This is the **default strategy** used by the navigation system. It always returns a scene,
 * making it suitable as a fallback when other strategies (like [ListDetailSceneStrategy] or
 * [TwoPaneSceneStrategy]) cannot determine an appropriate scene.
 *
 * The strategy extracts all [ComposeDestination] instances from the navigation stack and
 * creates a [SinglePaneScene] that displays only the top (most recent) destination.
 *
 * This strategy is typically used:
 * - As a fallback in a chain of strategies (e.g., `listDetailStrategy then SinglePaneSceneStrategy`)
 * - As the default strategy when no other strategies are provided
 * - For phone-sized screens where single-pane layouts are always appropriate
 *
 * Example usage:
 * ```
 * Scene(
 *     navigationHost = myNavigationHost,
 *     sceneStrategies = listOf(
 *         rememberListDetailSceneStrategy(),
 *         SinglePaneSceneStrategy, // Fallback
 *     ),
 * )
 * ```
 *
 * Or as a default:
 * ```
 * Scene(
 *     navigationHost = myNavigationHost,
 *     sceneStrategy = SinglePaneSceneStrategy, // Default strategy
 * )
 * ```
 */
public object SinglePaneSceneStrategy : SceneStrategy {

    override fun calculateScene(navigationHost: NavigationHost): Scene {
        val composeDestinations =
            navigationHost.stack.filter { it is ComposeDestination } as List<ComposeDestination>
        return SinglePaneScene(
            key = composeDestinations.last().contentKey,
            destinations = composeDestinations,
        )
    }
}

/**
 * A [Scene] implementation that displays a single destination at a time.
 *
 * This is the simplest scene type, displaying only the top (most recent) destination from
 * the navigation stack. It's the default scene used by [SinglePaneSceneStrategy] and is
 * suitable for phone-sized screens or as a fallback when multi-pane layouts are not available.
 *
 * The scene displays the last destination in the [destinations] list, which corresponds to
 * the current destination in the navigation stack.
 *
 * This scene is automatically created by [SinglePaneSceneStrategy], which is the default
 * strategy used by the navigation system when no other strategies are provided or when other
 * strategies cannot determine an appropriate scene.
 *
 * @property key A unique identifier for this scene instance, typically the current destination's content key.
 * @property destinations The complete list of destinations in the navigation stack.
 *                        Only the last destination is displayed.
 */
public class SinglePaneScene(
    override val key: Any,
    override val destinations: List<ComposeDestination>,
) : Scene {

    override val content: @Composable () -> Unit = {
        destinations.last().Content()
    }
}