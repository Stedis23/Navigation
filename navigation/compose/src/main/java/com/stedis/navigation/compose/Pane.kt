package com.stedis.navigation.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationHost

val LocalNavigationHost = compositionLocalOf<NavigationHost> {
    error("No NavigationHost provided")
}

val LocalDestination = compositionLocalOf<Destination> {
    error("No Destination provided")
}


/**
 * Composable function that displays the provided Compose destination screen.
 *
 * This function takes a [ComposeDestination] as a parameter and invokes its
 * [ComposeDestination.Content] function to render the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework,
 * allowing for seamless integration of destination screens within a Compose
 * hierarchy.
 *
 * State management for the destination is handled internally by [ComposeDestination.Content],
 * which uses a [SaveableStateHolder] provided via [LocalSaveableStateHolder] to save and restore
 * state during configuration changes, such as screen rotations.
 *
 * Navigation transitions between destinations are animated using [AnimatedContent],
 * with the animation specified by the [transitionSpec] parameter. By default,
 * a simple fade-in/fade-out animation is used, but this can be customized as needed.
 *
 * The destination is wrapped in a [Box] container, allowing for flexible layout customization
 * via the [modifier] parameter. The destination is also provided via [LocalDestination]
 * composition local, making it accessible to child composables.
 *
 * Example usage:
 * ```
 * Pane(destination = MyComposeDestination())
 * ```
 *
 * Note: Ensure that the [ComposeDestination] provided has been properly configured
 * and that [LocalSaveableStateHolder] is provided in the composition hierarchy.
 *
 * @param destination The [ComposeDestination] whose composable screen should be displayed.
 * This destination must be an instance of [ComposeDestination] and will have its
 * [ComposeDestination.Content] function invoked to render the UI.
 *
 * @param modifier An optional [Modifier] to be applied to the [Box] container of the
 * destination screen. This can be used to customize the layout, padding, or other
 * visual properties of the displayed screen.
 *
 * @param transitionSpec A [ContentTransform] defining the animation used when transitioning
 * to this destination. The default is a fade-in/fade-out animation. You can provide custom
 * animations to control the visual behavior during navigation transitions.
 */
@Composable
public fun Pane(
    destination: ComposeDestination,
    modifier: Modifier = Modifier,
    transitionSpec: ContentTransform = fadeIn().togetherWith(fadeOut()),
) {
    CompositionLocalProvider(LocalDestination provides destination) {
        AnimatedContent(
            targetState = destination,
            transitionSpec = { transitionSpec using null },
        ) { animatedDestination ->
            Box(modifier = modifier) {
                animatedDestination.Content()
            }
        }
    }
}

/**
 * Composable function that displays the current destination screen from the provided [NavigationHost].
 *
 * This function takes a [NavigationHost] as a parameter and retrieves the current destination
 * to invoke its [ComposeDestination.Content] function for rendering the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework, allowing for seamless
 * integration of destination screens within a Compose hierarchy.
 *
 * State management for the destination is handled internally by [ComposeDestination.Content],
 * which uses a [SaveableStateHolder] provided via [LocalSaveableStateHolder] to save and restore
 * state during configuration changes, such as screen rotations.
 *
 * Navigation between destinations is animated using [AnimatedContent], and the specific transition
 * animation is automatically selected based on the navigation action by comparing the previous and
 * current navigation hosts. The animation selection logic in [getTransitionSpec] determines the
 * appropriate animation based on:
 * - Stack size changes (forward/back navigation)
 * - Host name changes (host change navigation)
 * - Same host and stack size (replace navigation)
 *
 * The destination is wrapped in a [Column] container, allowing for flexible layout customization
 * via the [modifier] parameter. Both the [NavigationHost] and current [Destination] are provided
 * via [LocalNavigationHost] and [LocalDestination] composition locals, making them accessible
 * to child composables.
 *
 * Example usage:
 * ```
 * Pane(navigationHost = myNavigationHost)
 * ```
 *
 * Note: Ensure that the provided [NavigationHost] is properly configured, that its current
 * destination is a [ComposeDestination], and that [LocalSaveableStateHolder] is provided
 * in the composition hierarchy.
 *
 * @param navigationHost The [NavigationHost] that contains the current destination to be displayed.
 * This host must be configured to manage navigation within the application, and its current destination
 * must be a [ComposeDestination] instance.
 *
 * @param modifier An optional [Modifier] to be applied to the [Column] container of the destination screen.
 * This can be used to customize the layout, padding, or other visual properties of the displayed screen.
 *
 * @param navigationAnimations An instance of [NavigationAnimations] that defines the animations used
 * for transitions between destinations. This includes animations for forward navigation, back navigation,
 * replacing the current destination, changing hosts, and a default fallback animation. You can customize
 * these animations by providing your own [NavigationAnimations] instance.
 */
@Composable
public fun Pane(
    navigationHost: NavigationHost,
    modifier: Modifier = Modifier,
    navigationAnimations: NavigationAnimations = NavigationAnimations(),
) {

    var previousHost by remember { mutableStateOf(navigationHost) }

    LaunchedEffect(navigationHost) {
        previousHost = navigationHost
    }

    val currentDestination = navigationHost.currentDestination
    CompositionLocalProvider(
        LocalNavigationHost provides navigationHost,
        LocalDestination provides currentDestination,
    ) {
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = {
                getTransitionSpec(
                    previousHost,
                    navigationHost,
                    navigationAnimations,
                ) using null
            },
        ) { destination ->

            Column(modifier = modifier) {
                (destination as? ComposeDestination)?.Content()
            }
        }
    }
}

private fun getTransitionSpec(
    previousHost: NavigationHost,
    currentHost: NavigationHost,
    navigationAnimations: NavigationAnimations
): ContentTransform =
    when {
        previousHost.hostName == currentHost.hostName &&
                previousHost.stack.size < currentHost.stack.size -> navigationAnimations.backAnimation

        previousHost.hostName == currentHost.hostName &&
                previousHost.stack.size > currentHost.stack.size -> navigationAnimations.forwardAnimation

        previousHost.hostName == currentHost.hostName -> navigationAnimations.replaceAnimation

        previousHost.hostName != currentHost.hostName -> navigationAnimations.hostChangeAnimation

        else -> navigationAnimations.default
    }