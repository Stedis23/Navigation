package com.stedis.navigation.compose

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit

/**
 * Composable function that serves as a container for displaying fragments within a Jetpack Compose UI.
 *
 * This function creates a FragmentContainerView and manages the lifecycle of fragments using
 * the provided FragmentManager. It allows for seamless integration of traditional Android fragments
 * into a Compose-based application, enabling developers to leverage existing fragment-based
 * architecture while adopting Compose for new UI components.
 *
 * Example usage:
 * ```
 * FragmentContainer(fragmentManager = supportFragmentManager) { containerId ->
 *     replace(containerId, MyFragment())
 * }
 * ```
 *
 * @param modifier Optional [Modifier] to customize the appearance and layout of the container.
 * @param fragmentManager The [FragmentManager] used to manage fragment transactions within the container.
 * @param commit A lambda function that defines the fragment transaction to be performed. It receives
 * the ID of the container view and allows for custom fragment operations to be defined.
 */
@Composable
public fun FragmentContainer(
    modifier: Modifier = Modifier,
    fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val containerId by rememberSaveable { mutableIntStateOf(View.generateViewId()) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context)
                .apply { id = containerId }
        },
        update = { view ->
            fragmentManager.commit { commit(view.id) }
        }
    )
}