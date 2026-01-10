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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationHost

/**
 * Composition local that provides access to the current [NavigationHost] in the composition hierarchy.
 *
 * This is typically provided by [Pane] or [Scene] composables and can be accessed by child
 * composables to perform navigation operations or access navigation state.
 */
public val LocalNavigationHost = compositionLocalOf<NavigationHost> {
    error("No NavigationHost provided")
}

/**
 * Composition local that provides access to the current [Destination] in the composition hierarchy.
 *
 * This is typically provided by [Pane] composables and can be accessed by child composables
 * to access destination-specific information or metadata.
 */
public val LocalDestination = compositionLocalOf<Destination> {
    error("No Destination provided")
}


/**
 * Composable function that displays the provided Compose destination screen.
 *
 * This function takes a [ComposeDestination] as a parameter and invokes its
 * [ComposeDestination.Content] function to render the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework,
 * allowing for seamless integration of destination screens within a Compose hierarchy.
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
 * and that [LocalSaveableStateHolder] is provided in the composition hierarchy
 * (typically via the [Navigation] composable).
 *
 * @param destination The [ComposeDestination] whose composable screen should be displayed.
 *                    This destination must be an instance of [ComposeDestination] and will have
 *                    its [ComposeDestination.Content] function invoked to render the UI.
 * @param modifier An optional [Modifier] to be applied to the [Box] container of the
 *                 destination screen. This can be used to customize the layout, padding, or other
 *                 visual properties of the displayed screen.
 * @param transitionSpec A [ContentTransform] defining the animation used when transitioning
 *                       to this destination. The default is a fade-in/fade-out animation.
 *                       You can provide custom animations to control the visual behavior during
 *                       navigation transitions.
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
 * If the current destination has custom animations specified in its metadata (via [setAnimationSpec]),
 * those animations will be used instead of the [defaultNavigationAnimations].
 *
 * Example usage:
 * ```
 * Pane(navigationHost = myNavigationHost)
 * ```
 *
 * Note: Ensure that the provided [NavigationHost] is properly configured, that its current
 * destination is a [ComposeDestination], and that [LocalSaveableStateHolder] is provided
 * in the composition hierarchy (typically via the [Navigation] composable).
 *
 * @param navigationHost The [NavigationHost] that contains the current destination to be displayed.
 *                      This host must be configured to manage navigation within the application,
 *                      and its current destination must be a [ComposeDestination] instance.
 * @param modifier An optional [Modifier] to be applied to the [Column] container of the destination screen.
 *                 This can be used to customize the layout, padding, or other visual properties
 *                 of the displayed screen.
 * @param defaultNavigationAnimations An instance of [NavigationAnimations] that defines the animations
 *                                   used for transitions between destinations. This includes animations
 *                                   for forward navigation, back navigation, replacing the current destination,
 *                                   changing hosts, and a default fallback animation. You can customize
 *                                   these animations by providing your own [NavigationAnimations] instance.
 *                                   If a destination has custom animations in its metadata, those take precedence.
 */
@Composable
public fun Pane(
    navigationHost: NavigationHost,
    modifier: Modifier = Modifier,
    defaultNavigationAnimations: NavigationAnimations = getDefaultNavigationAnimations(),
) {
    var previousHost by remember { mutableStateOf(navigationHost) }

    LaunchedEffect(navigationHost) {
        previousHost = navigationHost
    }

    val currentDestination = navigationHost.currentDestination as? ComposeDestination
    currentDestination?.let {
        CompositionLocalProvider(
            LocalNavigationHost provides navigationHost,
            LocalDestination provides it as Destination,
        ) {
            val transitionSpec = getTransitionSpec(
                previousHost,
                navigationHost,
                it.contentAnimations() ?: defaultNavigationAnimations,
            )

            AnimatedContent(
                targetState = it,
                transitionSpec = { transitionSpec.first using transitionSpec.second },
            ) { destination ->
                Column(modifier = modifier) {
                    destination.Content()
                }
            }
        }
    }
}

/**
 * Retrieves the custom navigation animations from the destination's metadata, if available.
 *
 * This function checks if the destination has custom animations specified via [setAnimationSpec]
 * in its metadata. If found, these animations will be used instead of the default animations
 * provided to [Pane].
 *
 * @return The [NavigationAnimations] from metadata, or `null` if not specified.
 */
private fun ComposeDestination.contentAnimations(): NavigationAnimations? {
    return metadata[ANIMATIONS_METADATA_KEY] as? NavigationAnimations
}