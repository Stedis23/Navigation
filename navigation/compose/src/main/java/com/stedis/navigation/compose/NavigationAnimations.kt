package com.stedis.navigation.compose

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import com.stedis.navigation.core.NavigationHost

internal const val ANIMATIONS_METADATA_KEY = "com.stedis.navigation.compose.NavigationAnimations"

/**
 * Creates a metadata map containing custom navigation animations for a destination.
 *
 * This function wraps a [NavigationAnimations] instance in a metadata map that can be assigned
 * to a [ComposeDestination]'s `metadata` property. When a destination has custom animations
 * specified, they will be used instead of the default animations provided to [Pane] or [Scene].
 *
 * The animation selection is automatic based on the navigation action:
 * - [NavigationAnimations.stackForwardAnimation] is used when navigating forward (stack size increases)
 * - [NavigationAnimations.stackPopAnimation] is used when navigating backward (stack size decreases)
 * - [NavigationAnimations.stackReplaceAnimation] is used when replacing the current destination
 * - [NavigationAnimations.hostChangeAnimation] is used when switching between different navigation hosts
 * - [NavigationAnimations.default] is used as a fallback for any other cases
 *
 * Example usage in a [ComposeDestination]:
 * ```
 * @Parcelize
 * object MainDestination : ComposeDestination() {
 *     override val content: @Composable () -> Unit = {
 *         MainPane()
 *     }
 *
 *     override val metadata: Map<Any, Any> = setAnimationSpec(
 *         NavigationAnimations(
 *             stackForwardAnimation =
 *                 slideInHorizontally(
 *                     initialOffsetX = { fullWidth -> fullWidth },
 *                     animationSpec = tween()
 *                 ).togetherWith(
 *                     slideOutHorizontally(
 *                         targetOffsetX = { fullWidth -> -fullWidth },
 *                         animationSpec = tween()
 *                     )
 *                 ) to SizeTransform(clip = false),
 *         )
 *     )
 * }
 * ```
 *
 * You can customize only specific animations and leave others with their default values:
 * ```
 * override val metadata: Map<Any, Any> = setAnimationSpec(
 *     NavigationAnimations(
 *         stackForwardAnimation = customSlideAnimation to null,
 *         // Other animations will use their default values
 *     )
 * )
 * ```
 *
 * @param navigationAnimations The [NavigationAnimations] instance containing custom animation
 *                             definitions. If not provided, defaults to [NavigationAnimations]
 *                             with all default animations.
 * @return A metadata map that can be assigned to a [ComposeDestination]'s `metadata` property.
 */
public fun setAnimationSpec(navigationAnimations: NavigationAnimations = NavigationAnimations()): Map<Any, Any> =
    mapOf(ANIMATIONS_METADATA_KEY to navigationAnimations)

/**
 * Data class that holds animation definitions for different types of navigation transitions.
 *
 * This class allows you to customize animations for various navigation scenarios. Each property
 * defines a [Pair] of [ContentTransform] (the animation itself) and an optional [SizeTransform]
 * (for size-based animations). If [SizeTransform] is `null`, no size animation is applied.
 *
 * The navigation system automatically selects the appropriate animation based on the navigation action:
 * - When navigating forward (pushing a new destination), [stackForwardAnimation] is used
 * - When navigating backward (popping the current destination), [stackPopAnimation] is used
 * - When replacing the current destination, [stackReplaceAnimation] is used
 * - When switching between different navigation hosts, [hostChangeAnimation] is used
 * - For any other cases, [default] is used as a fallback
 *
 * All properties have default values, so you can customize only the animations you need.
 * The default animations use horizontal slide transitions for forward/back navigation and
 * fade transitions for other cases.
 *
 * Example usage:
 * ```
 * val customAnimations = NavigationAnimations(
 *     stackForwardAnimation = slideInHorizontally(
 *         initialOffsetX = { fullWidth -> fullWidth },
 *         animationSpec = tween(durationMillis = 300)
 *     ).togetherWith(
 *         slideOutHorizontally(
 *             targetOffsetX = { fullWidth -> -fullWidth },
 *             animationSpec = tween(durationMillis = 300)
 *         )
 *     ) to SizeTransform(clip = false),
 *     stackPopAnimation = slideInHorizontally(
 *         initialOffsetX = { fullWidth -> -fullWidth },
 *         animationSpec = tween(durationMillis = 300)
 *     ).togetherWith(
 *         slideOutHorizontally(
 *             targetOffsetX = { fullWidth -> fullWidth },
 *             animationSpec = tween(durationMillis = 300)
 *         )
 *     ) to null,
 * )
 * ```
 *
 * @property hostChangeAnimation Animation played when the navigation host changes.
 *                               This occurs when switching between different navigation contexts
 *                               (e.g., switching between tabs or different navigation graphs).
 *                               Default: fade in/out animation.
 * @property stackPopAnimation Animation played when navigating backward in the navigation stack
 *                            (e.g., when the user presses back or calls a pop operation).
 *                            Default: slide in from left, slide out to right.
 * @property stackForwardAnimation Animation played when navigating forward in the navigation stack
 *                                (e.g., when pushing a new destination onto the stack).
 *                                Default: slide in from right, slide out to left.
 * @property stackReplaceAnimation Animation played when replacing the current destination within
 *                                the same host (e.g., when replacing the top destination without
 *                                changing the stack size).
 *                                Default: slide in from right, slide out to left.
 * @property default Default animation used as a fallback when none of the specific animations
 *                  match the navigation scenario. This should rarely be needed in practice.
 *                  Default: fade in/out animation.
 */
public data class NavigationAnimations(
    val hostChangeAnimation: Pair<ContentTransform, SizeTransform?> = defaultAnimationSpec() to null,
    val stackPopAnimation: Pair<ContentTransform, SizeTransform?> =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween()
        ).togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween()
            )
        ) to null,
    val stackForwardAnimation: Pair<ContentTransform, SizeTransform?> =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween()
        ).togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween()
            )
        ) to null,
    val stackReplaceAnimation: Pair<ContentTransform, SizeTransform?> =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween()
        ).togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween()
            )
        ) to null,
    val default: Pair<ContentTransform, SizeTransform?> = defaultAnimationSpec() to null,
)

/**
 * Creates a [NavigationAnimations] instance with all animations set to default fade transitions.
 *
 * This function returns a [NavigationAnimations] instance where all animation types use
 * the default fade in/out animation. This is useful when you want consistent, simple
 * animations for all navigation transitions without customizing individual animation types.
 *
 * The default animations are:
 * - All use fade in/out transitions
 * - No size transformations are applied
 *
 * This is the default value used by [Pane] and [Scene] composables when no custom
 * animations are specified in a destination's metadata.
 *
 * Example usage:
 * ```
 * // Use default animations for a pane
 * Pane(
 *     navigationHost = myNavigationHost,
 *     defaultNavigationAnimations = getDefaultNavigationAnimations()
 * )
 * ```
 *
 * @return A [NavigationAnimations] instance with all animations set to default fade transitions.
 */
public fun getDefaultNavigationAnimations(): NavigationAnimations =
    NavigationAnimations(
        hostChangeAnimation = defaultAnimationSpec() to null,
        stackPopAnimation = defaultAnimationSpec() to null,
        stackForwardAnimation = defaultAnimationSpec() to null,
        stackReplaceAnimation = defaultAnimationSpec() to null,
        default = defaultAnimationSpec() to null,
    )

/**
 * Determines the appropriate animation to use based on the navigation state change.
 *
 * This function compares the previous and current navigation hosts to determine which
 * type of navigation transition occurred, and returns the corresponding animation from
 * the provided [NavigationAnimations] instance.
 *
 * The selection logic:
 * 1. If the host name is the same and stack size decreased → [stackPopAnimation]
 * 2. If the host name is the same and stack size increased → [stackForwardAnimation]
 * 3. If the host name is the same and stack size unchanged → [stackReplaceAnimation]
 * 4. If the host name changed → [hostChangeAnimation]
 * 5. Otherwise → [default]
 *
 * @param previousHost The navigation host before the transition.
 * @param currentHost The navigation host after the transition.
 * @param navigationAnimations The animations to choose from.
 * @return A pair of [ContentTransform] and optional [SizeTransform] for the transition.
 */
internal fun getTransitionSpec(
    previousHost: NavigationHost,
    currentHost: NavigationHost,
    navigationAnimations: NavigationAnimations
): Pair<ContentTransform, SizeTransform?> =
    when {
        previousHost.hostName == currentHost.hostName &&
                previousHost.stack.size > currentHost.stack.size -> navigationAnimations.stackPopAnimation

        previousHost.hostName == currentHost.hostName &&
                previousHost.stack.size < currentHost.stack.size -> navigationAnimations.stackForwardAnimation

        previousHost.hostName == currentHost.hostName -> navigationAnimations.stackReplaceAnimation

        previousHost.hostName != currentHost.hostName -> navigationAnimations.hostChangeAnimation

        else -> navigationAnimations.default
    }

/**
 * Creates a default fade in/out animation specification.
 *
 * This is used as the default animation for various navigation transitions when
 * no custom animation is specified.
 *
 * @return A [ContentTransform] that fades in the new content while fading out the old content.
 */
internal fun defaultAnimationSpec(): ContentTransform =
    fadeIn().togetherWith(fadeOut())