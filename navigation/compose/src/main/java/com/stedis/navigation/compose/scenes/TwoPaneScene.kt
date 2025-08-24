package com.stedis.navigation.compose.scenes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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

public object TwoPaneSceneStrategy : SceneStrategy {

    @Composable
    override fun calculateScene(navigationHost: NavigationHost): Scene? {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val lastTwoDestinations = navigationHost.stack.takeLast(2)
        return if (isValidTwoPaneConfiguration(lastTwoDestinations)) {
            TwoPaneScene()
        } else {
            null
        }
    }

    private fun isValidTwoPaneConfiguration(destinations: List<Destination>): Boolean {
        return destinations.size == 2 && destinations.all {
            val destination = (it as? ComposeDestination) ?: return false
            destination.metadata.containsKey(TwoPaneScene.TWO_PANE_KEY)
        }
    }
}

public class TwoPaneScene : Scene {
    override val composable: @Composable ((NavigationHost) -> Unit) = { host ->
        val previousStack = host.stack.dropLast(1)
        val previousHost =
            host.copy(stack = previousStack, currentDestination = previousStack.last())

        Row(Modifier.fillMaxSize()) {
            PreviousPane(
                modifier = Modifier.weight(0.5f),
                navigationHost = previousHost
            )

            CurrentPane(
                modifier = Modifier.weight(0.5f),
                navigationHost = host
            )
        }
    }

    @Composable
    private fun PreviousPane(
        modifier: Modifier,
        navigationHost: NavigationHost
    ) {
        Box(modifier = modifier) {
            Pane(
                navigationHost = navigationHost,
                key = PaneKey.PREVIOUS_PANE,
                navigationAnimations = createPreviousPaneAnimations()
            )
        }
    }

    @Composable
    private fun CurrentPane(
        modifier: Modifier,
        navigationHost: NavigationHost
    ) {
        Box(modifier = modifier) {
            Pane(
                navigationHost = navigationHost,
                key = PaneKey.CURRENT_PANE,
                navigationAnimations = createCurrentPaneAnimations()
            )
        }
    }

    private fun createPreviousPaneAnimations(): NavigationAnimations {
        return NavigationAnimations(
            backAnimation = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(easing = LinearEasing)
            ).togetherWith(fadeOut(tween(easing = LinearEasing))),

            forwardAnimation = fadeIn(tween(easing = LinearEasing, delayMillis = 300))
                .togetherWith(
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(easing = LinearEasing)
                    )
                )
        )
    }

    private fun createCurrentPaneAnimations(): NavigationAnimations {
        return NavigationAnimations(
            forwardAnimation = slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(easing = LinearEasing)
            ).togetherWith(fadeOut(tween(easing = LinearEasing))),

            backAnimation = fadeIn(tween(easing = LinearEasing, delayMillis = 300))
                .togetherWith(fadeOut(tween(durationMillis = 0)))
        )
    }

    public companion object {
        internal const val TWO_PANE_KEY = "TwoPane"

        public fun twoPane(): Map<Any, Any> = mapOf(TWO_PANE_KEY to true)
    }
}