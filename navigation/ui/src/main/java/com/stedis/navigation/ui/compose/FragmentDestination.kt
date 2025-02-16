package com.stedis.navigation.ui.compose

import androidx.fragment.app.Fragment
import com.stedis.navigation.core.Destination

/**
 * Interface representing a destination that is intended for fragment navigation.
 *
 * This interface extends the base [Destination] and provides a method for creating
 * an instance of a [Fragment]. Implementing classes must define how to instantiate
 * the appropriate Fragment for the destination, enabling the integration of
 * traditional Android fragments within a navigation system.
 *
 * Example usage:
 * ```
 * @Parcelize
 * class SampleDestination : FragmentDestination {
 *
 *     override fun createFragment(): Fragment =
 *         SampleFragment.newInstance()
 * }
 * ```
 */
interface FragmentDestination : Destination {

    public fun createFragment(): Fragment
}