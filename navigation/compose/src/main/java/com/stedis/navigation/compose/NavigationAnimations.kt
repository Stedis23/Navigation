package com.stedis.navigation.compose

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

/**
 * Data class that holds animation definitions for different types of navigation transitions.
 *
 * @property hostChangeAnimation Animation played when the navigation host changes.
 * @property backAnimation Animation played when navigating backward in the navigation stack.
 * @property forwardAnimation Animation played when navigating forward in the navigation stack.
 * @property replaceAnimation Animation played when replacing the current destination within the same host.
 * @property default Default animation used as a fallback.
 */
data class NavigationAnimations(
    val hostChangeAnimation: ContentTransform =
        fadeIn().togetherWith(fadeOut()),
    val backAnimation: ContentTransform =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween()
        ).togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween()
            )
        ),
    val forwardAnimation: ContentTransform =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween()
        ).togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween()
            )
        ),
    val replaceAnimation: ContentTransform =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween()
        ).togetherWith(
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween()
            )
        ),
    val default: ContentTransform =
        fadeIn().togetherWith(fadeOut()),
)