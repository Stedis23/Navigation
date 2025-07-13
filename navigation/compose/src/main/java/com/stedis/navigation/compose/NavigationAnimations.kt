package com.stedis.navigation.compose

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

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