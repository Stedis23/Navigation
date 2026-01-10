package com.stedis.navigation.compose.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
 * A [Scene] implementation that displays two destinations side by side in equal-width panes.
 *
 * This scene is designed for tablet and large screen devices where there's enough space to
 * display two destinations simultaneously. The layout consists of:
 * - A left pane (50% width) that displays the first destination
 * - A right pane (50% width) that displays the second destination
 *
 * Both panes are displayed side by side in a horizontal [Row] layout with equal weights.
 *
 * To use this scene, both destinations must be marked with the [twoPane()] metadata:
 * - Both the first and second destinations must have [twoPane()] in their metadata
 * - The destinations must be the last two destinations in the navigation stack
 *
 * Example usage:
 * ```
 * @Parcelize
 * class FirstDestination : ComposeDestination() {
 *     override val content: @Composable () -> Unit = { FirstPane() }
 *     override val metadata: Map<Any, Any> = TwoPaneScene.twoPane()
 * }
 *
 * @Parcelize
 * class SecondDestination : ComposeDestination() {
 *     override val content: @Composable () -> Unit = { SecondPane() }
 *     override val metadata: Map<Any, Any> = TwoPaneScene.twoPane()
 * }
 * ```
 *
 * This scene is typically used with [TwoPaneSceneStrategy] which automatically selects
 * it when the screen width is at least medium breakpoint and both last two destinations
 * are marked with the two-pane metadata.
 *
 * @property key A unique identifier for this scene instance, typically a [Pair] of both
 *               destination content keys.
 * @property firstDestination The destination displayed in the left pane.
 * @property secondDestination The destination displayed in the right pane.
 */
public class TwoPaneScene(
    override val key: Any,
    public val firstDestination: ComposeDestination,
    public val secondDestination: ComposeDestination,
) : Scene {
    override val destinations: List<ComposeDestination> =
        listOf(firstDestination, secondDestination)

    override val content: @Composable () -> Unit = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.5f)) {
                firstDestination.Content()
            }
            Column(modifier = Modifier.weight(0.5f)) {
                secondDestination.Content()
            }
        }
    }

    override val metadata: Map<Any, Any> = emptyMap()

    public companion object {
        internal const val TWO_PANE_KEY = "TwoPane"

        /**
         * Creates metadata for a destination that should be displayed in a two-pane layout.
         *
         * This function returns a metadata map that marks a destination as part of a two-pane scene.
         * When used in a destination's metadata, it indicates that this destination can be displayed
         * alongside another destination in a [TwoPaneScene].
         *
         * For a [TwoPaneScene] to be created, both the last two destinations in the navigation stack
         * must have this metadata marker.
         *
         * Example usage:
         * ```
         * @Parcelize
         * class FirstDestination : ComposeDestination() {
         *     override val content: @Composable () -> Unit = { FirstPane() }
         *     override val metadata: Map<Any, Any> = TwoPaneScene.twoPane() + setAnimationSpec()
         * }
         *
         * @Parcelize
         * class SecondDestination : ComposeDestination() {
         *     override val content: @Composable () -> Unit = { SecondPane() }
         *     override val metadata: Map<Any, Any> = TwoPaneScene.twoPane() + setAnimationSpec()
         * }
         * ```
         *
         * @return A metadata map containing the two-pane marker.
         */
        public fun twoPane(): Map<Any, Any> = mapOf(TWO_PANE_KEY to true)
    }
}

/**
 * Creates and remembers a [TwoPaneSceneStrategy] instance based on the current window size.
 *
 * This Composable function automatically tracks the current window size class and creates
 * a [TwoPaneSceneStrategy] that will only activate when the screen width is at least
 * the medium breakpoint (typically 600dp for tablets).
 *
 * The strategy is recreated whenever the window size class changes, ensuring that the
 * two-pane layout is only used when there's sufficient screen space.
 *
 * Example usage:
 * ```
 * @Composable
 * fun MyNavigationContent(navigationHost: NavigationHost) {
 *     val twoPaneStrategy = rememberTwoPaneSceneStrategy()
 *
 *     Scene(
 *         navigationHost = navigationHost,
 *         sceneStrategies = listOf(
 *             twoPaneStrategy,
 *             SinglePaneSceneStrategy,
 *         ),
 *     )
 * }
 * ```
 *
 * @return A [TwoPaneSceneStrategy] instance that is remembered across recompositions
 *         and updated when the window size class changes.
 */
@Composable
public fun rememberTwoPaneSceneStrategy(): TwoPaneSceneStrategy {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return remember(windowSizeClass) {
        TwoPaneSceneStrategy(windowSizeClass)
    }
}

/**
 * A [SceneStrategy] that determines when to display a [TwoPaneScene].
 *
 * This strategy activates only when:
 * 1. The screen width is at least the medium breakpoint ([WIDTH_DP_MEDIUM_LOWER_BOUND])
 * 2. The navigation stack contains at least two destinations
 * 3. Both of the last two destinations are marked with [twoPane()] metadata
 *
 * When all conditions are met, it creates a [TwoPaneScene] that displays:
 * - The second-to-last destination in the left pane
 * - The last destination in the right pane
 *
 * If any condition is not met, the strategy returns `null`, allowing other strategies
 * (like [SinglePaneSceneStrategy]) to handle the navigation.
 *
 * This strategy is typically used in a chain with other strategies:
 * ```
 * val strategies = listOf(
 *     rememberTwoPaneSceneStrategy(),
 *     SinglePaneSceneStrategy,
 * )
 * ```
 *
 * @property windowSizeClass The window size class used to determine if the screen is large enough
 *                           for a two-pane layout.
 */
public class TwoPaneSceneStrategy(
    public val windowSizeClass: WindowSizeClass,
) : SceneStrategy {

    override fun calculateScene(navigationHost: NavigationHost): Scene? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val composeDestinations =
            navigationHost.stack.filter { it is ComposeDestination } as List<ComposeDestination>

        val lastTwoDestinations = composeDestinations.takeLast(2)

        return if (
            lastTwoDestinations.size == 2 &&
            lastTwoDestinations.all { it.metadata.containsKey(TwoPaneScene.TWO_PANE_KEY) }
        ) {
            val firstEntry = lastTwoDestinations.first()
            val secondEntry = lastTwoDestinations.last()

            val sceneKey = Pair(firstEntry.contentKey, secondEntry.contentKey)

            TwoPaneScene(
                key = sceneKey,
                firstDestination = firstEntry,
                secondDestination = secondEntry,
            )
        } else {
            null
        }
    }
}