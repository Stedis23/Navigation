package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable
import com.stedis.navigation.core.Destination

/**
 * Interface representing a destination that is designed for Compose screens.
 *
 * This interface extends the base [Destination] and provides a property for defining
 * a composable screen that can be rendered within a Jetpack Compose UI. Implementing
 * classes must provide a desired composable screen that takes a [Destination] as a parameter.
 *
 * Example usage:
 * ```
 * @Parcelize
 *  class SampleDestination : ComposeDestination {
 *
 *      override val composable: @Composable (Destination) -> Unit = {
 *          SampleScreen() // composable function
 *     }
 * }
 *```
 */
interface ComposeDestination : Destination {

    public val composable: @Composable (Destination) -> Unit
}