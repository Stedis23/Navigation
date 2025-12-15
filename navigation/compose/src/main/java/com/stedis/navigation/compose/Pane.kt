package com.stedis.navigation.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationHost

val LocalSaveableStateHolder = compositionLocalOf<SaveableStateHolder> {
    error("No SaveableStateHolder provided")
}

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
 * defined composable function to render the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework,
 * allowing for seamless integration of destination screens within a Compose
 * hierarchy.
 *
 * The function uses a [SaveableStateHolder] to manage the state of the destination
 * screen, ensuring that any state associated with the screen is saved and restored
 * appropriately during configuration changes, such as screen rotations.
 *
 * Navigation transitions between destinations are animated using [AnimatedContent],
 * with the animation specified by the [transitionSpec] parameter. By default,
 * a simple fade-in/fade-out animation is used, but this can be customized as needed.
 *
 * Example usage:
 * ```
 * Pane(destination = MyComposeDestination())
 * ```
 *
 * Note: Ensure that the [ComposeDestination] provided has been properly configured
 * within the navigation system, and that its composable function is capable of handling
 * the required parameters and state.
 *
 * @param destination The [ComposeDestination] whose composable screen should be displayed.
 * This destination must implement the [ComposeDestination] interface and provide
 * a valid composable function. The composable function will be invoked with the
 * destination as a parameter, allowing it to access any necessary data or state.
 *
 * @param modifier An optional [Modifier] to be applied to the container of the
 * destination screen. This can be used to customize the layout, padding, or other
 * visual properties of the displayed screen.
 *
 * @param transitionSpec A [ContentTransform] defining the animation used when transitioning
 * to this destination. The default is a fade-in/fade-out animation. You can provide custom
 * animations to control the visual behavior during navigation transitions.
 *
 * @param key An optional key component to add to the state save/restore key. This is useful to force state reset
 * for the same destination if it should be treated as a distinct instance (e.g., viewing different items by ID).
 * The final key is `destination.toString() + key`.
 */
@Composable
public fun Pane(
    destination: ComposeDestination,
    modifier: Modifier = Modifier,
    transitionSpec: ContentTransform = fadeIn().togetherWith(fadeOut()),
    key: String? = null,
) {
    val saveableStateHolder = LocalSaveableStateHolder.current
    val stateKey = destination.toString() + (key ?: "")
    saveableStateHolder.SaveableStateProvider(
        key = stateKey,
        content = {
            CompositionLocalProvider(LocalDestination provides destination) {
                AnimatedContent(
                    targetState = destination,
                    transitionSpec = { transitionSpec using null },
                ) { destination ->
                    Box(modifier = modifier) {
                        destination.content()
                    }
                }
            }
        }
    )
}

/**
 * Composable function that displays the current destination screen from the provided [NavigationHost].
 *
 * This function takes a [NavigationHost] as a parameter and retrieves the current destination
 * to invoke its defined composable function for rendering the corresponding UI. It serves as a
 * bridge between the navigation system and the Jetpack Compose framework, allowing for seamless
 * integration of destination screens within a Compose hierarchy.
 *
 * The function utilizes a [SaveableStateHolder] to manage the state of the displayed destination
 * screen, ensuring that any state associated with the screen is saved and restored appropriately
 * during configuration changes, such as screen rotations.
 *
 * Navigation between destinations is animated using [AnimatedContent], and the specific transition
 * animation is selected based on the navigation action (forward, back, replace, host change) via
 * the provided [navigationAnimations] parameter.
 *
 * Example usage:
 * ```
 * Pane(navigationHost = myNavigationHost)
 * ```
 *
 * Note: Ensure that the provided [NavigationHost] is properly configured and that its current
 * destination implements the [ComposeDestination] interface, providing a valid composable function
 * that can handle any required parameters and state.
 *
 * @param navigationHost The [NavigationHost] that contains the current destination to be displayed.
 * This host must be configured to manage navigation within the application, and its current destination
 * must implement the [ComposeDestination] interface.
 *
 * @param modifier An optional [Modifier] to be applied to the container of the destination screen.
 * This can be used to customize the layout, padding, or other visual properties of the displayed screen.
 *
 * @param navigationAnimations An instance of [NavigationAnimations] that defines the animations used
 * for transitions between destinations. This includes animations for forward navigation, back navigation,
 * replacing the current destination, changing hosts, and a default fallback animation. You can customize
 * these animations by providing your own [NavigationAnimations] instance.
 *
 * @param key An optional key component added to the state save/restore key for the current destination.
 * See the other [Pane] overload for details.
 *
 */
@Composable
public fun Pane(
    navigationHost: NavigationHost,
    modifier: Modifier = Modifier,
    navigationAnimations: NavigationAnimations = NavigationAnimations(),
    key: String? = null,
) {
    val saveableStateHolder = LocalSaveableStateHolder.current
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
            saveableStateHolder.SaveableStateProvider(
                key = destination.toString() + (key ?: ""),
                content = {
                    Box(modifier = modifier) {
                        (destination as? ComposeDestination)?.content()
                    }
                }
            )
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