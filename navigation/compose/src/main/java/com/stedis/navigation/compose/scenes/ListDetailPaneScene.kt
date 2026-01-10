package com.stedis.navigation.compose.scenes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.Scene
import com.stedis.navigation.compose.SceneStrategy
import com.stedis.navigation.core.NavigationHost

/**
 * A [Scene] implementation that displays a list-detail layout with two panes side by side.
 *
 * This scene is designed for tablet and large screen devices where there's enough space to
 * display both a list and its detail view simultaneously. The layout consists of:
 * - A list pane on the left (40% width) that displays the list destination
 * - A vertical divider in the middle
 * - A detail pane on the right (60% width) that displays the detail destination with fade animations
 *
 * The list pane remains static, while the detail pane animates when switching between
 * different detail destinations.
 *
 * To use this scene, destinations must be marked with the appropriate metadata:
 * - List destinations should use [listPane()] in their metadata
 * - Detail destinations should use [detailPane()] in their metadata
 *
 * Example usage:
 * ```
 * @Parcelize
 * class FriendsFeedDestination : ComposeDestination() {
 *     override val content: @Composable () -> Unit = { FriendsFeedPane() }
 *     override val metadata: Map<Any, Any> = ListDetailScene.listPane() + setAnimationSpec()
 * }
 *
 * @Parcelize
 * class FriendInfoDestination(val friendId: Int) : ComposeDestination() {
 *     override val content: @Composable () -> Unit = { FriendInfoPane(friendId = friendId) }
 *     override val metadata: Map<Any, Any> = ListDetailScene.detailPane() + setAnimationSpec()
 * }
 * ```
 *
 * This scene is typically used with [ListDetailSceneStrategy] which automatically selects
 * it when the screen width is at least medium breakpoint and both list and detail destinations
 * are present in the navigation stack.
 *
 * @property listDestination The destination that represents the list pane (left side).
 * @property detailDestination The destination that represents the detail pane (right side).
 * @property key A unique identifier for this scene instance, typically the list destination's content key.
 * @property destinations The complete list of destinations in the navigation stack.
 */
public class ListDetailScene(
    public val listDestination: ComposeDestination,
    public val detailDestination: ComposeDestination,
    override val key: Any,
    override val destinations: List<ComposeDestination>,
) : Scene {

    override val content: @Composable () -> Unit = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.4f)) {
                listDestination.Content()
            }

            VerticalDivider()

            Column(modifier = Modifier.weight(0.6f)) {
                AnimatedContent(
                    targetState = detailDestination,
                    contentKey = { entry -> entry.contentKey },
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                ) { entry ->
                    entry.Content()
                }
            }
        }
    }

    override val metadata: Map<Any, Any> = emptyMap()

    public companion object {
        internal const val LIST_KEY = "ListDetailScene-List"
        internal const val DETAIL_KEY = "ListDetailScene-Detail"

        /**
         * Creates metadata for a destination that should be displayed in the list pane.
         *
         * This function returns a metadata map that marks a destination as a list pane.
         * When used in a destination's metadata, it indicates that this destination should
         * be displayed on the left side of a [ListDetailScene] when the scene is active.
         *
         * Example usage:
         * ```
         * @Parcelize
         * class FriendsFeedDestination : ComposeDestination() {
         *     override val content: @Composable () -> Unit = { FriendsFeedPane() }
         *     override val metadata: Map<Any, Any> = ListDetailScene.listPane() + setAnimationSpec()
         * }
         * ```
         *
         * @return A metadata map containing the list pane marker.
         */
        public fun listPane(): Map<Any, Any> = mapOf(LIST_KEY to true)

        /**
         * Creates metadata for a destination that should be displayed in the detail pane.
         *
         * This function returns a metadata map that marks a destination as a detail pane.
         * When used in a destination's metadata, it indicates that this destination should
         * be displayed on the right side of a [ListDetailScene] when the scene is active.
         *
         * Example usage:
         * ```
         * @Parcelize
         * class FriendInfoDestination(val friendId: Int) : ComposeDestination() {
         *     override val content: @Composable () -> Unit = { FriendInfoPane(friendId = friendId) }
         *     override val metadata: Map<Any, Any> = ListDetailScene.detailPane() + setAnimationSpec()
         * }
         * ```
         *
         * @return A metadata map containing the detail pane marker.
         */
        public fun detailPane(): Map<Any, Any> = mapOf(DETAIL_KEY to true)
    }
}

/**
 * Creates and remembers a [ListDetailSceneStrategy] instance based on the current window size.
 *
 * This Composable function automatically tracks the current window size class and creates
 * a [ListDetailSceneStrategy] that will only activate when the screen width is at least
 * the medium breakpoint (typically 600dp for tablets).
 *
 * The strategy is recreated whenever the window size class changes, ensuring that the
 * list-detail layout is only used when there's sufficient screen space.
 *
 * Example usage:
 * ```
 * @Composable
 * fun MyNavigationContent(navigationHost: NavigationHost) {
 *     val listDetailStrategy = rememberListDetailSceneStrategy()
 *
 *     Scene(
 *         navigationHost = navigationHost,
 *         sceneStrategies = listOf(
 *             listDetailStrategy,
 *             SinglePaneSceneStrategy,
 *         ),
 *     )
 * }
 * ```
 *
 * @return A [ListDetailSceneStrategy] instance that is remembered across recompositions
 *         and updated when the window size class changes.
 */
@Composable
public fun rememberListDetailSceneStrategy(): ListDetailSceneStrategy {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return remember(windowSizeClass) {
        ListDetailSceneStrategy(windowSizeClass)
    }
}

/**
 * A [SceneStrategy] that determines when to display a [ListDetailScene].
 *
 * This strategy activates only when:
 * 1. The screen width is at least the medium breakpoint ([WIDTH_DP_MEDIUM_LOWER_BOUND])
 * 2. The navigation stack contains at least one destination marked as a list pane
 * 3. The current (top) destination is marked as a detail pane
 *
 * When all conditions are met, it creates a [ListDetailScene] that displays:
 * - The most recent list destination in the left pane
 * - The current detail destination in the right pane
 *
 * If any condition is not met, the strategy returns `null`, allowing other strategies
 * (like [SinglePaneSceneStrategy]) to handle the navigation.
 *
 * This strategy is typically used in a chain with other strategies:
 * ```
 * val strategies = listOf(
 *     rememberListDetailSceneStrategy(),
 *     SinglePaneSceneStrategy,
 * )
 * ```
 *
 * @property windowSizeClass The window size class used to determine if the screen is large enough
 *                           for a list-detail layout.
 */
public class ListDetailSceneStrategy(
    public val windowSizeClass: WindowSizeClass,
) : SceneStrategy {

    override fun calculateScene(navigationHost: NavigationHost): Scene? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val composeDestinations =
            navigationHost.stack.filter { it is ComposeDestination } as List<ComposeDestination>

        val detailDestination = composeDestinations.lastOrNull()
            ?.takeIf { it.metadata.containsKey(ListDetailScene.DETAIL_KEY) }
            ?: return null

        val listDestination =
            composeDestinations.findLast { it.metadata.containsKey(ListDetailScene.LIST_KEY) }
                ?: return null

        return ListDetailScene(
            listDestination = listDestination,
            detailDestination = detailDestination,
            key = listDestination.contentKey,
            destinations = composeDestinations,
        )
    }
}
