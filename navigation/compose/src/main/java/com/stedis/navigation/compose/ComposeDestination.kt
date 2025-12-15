package com.stedis.navigation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import com.stedis.navigation.core.Destination

/**
 * Abstract class representing a destination that is designed for Compose screens.
 *
 * This class extends the base [Destination] and provides functionality for defining
 * and rendering composable screens within a Jetpack Compose UI. Subclasses must provide
 * a [content] property that defines the composable screen to be displayed.
 *
 * The [Content] function handles the rendering and state management of the destination.
 * It uses [movableContentOf] to optimize recomposition when the destination moves between
 * different positions in the composition tree, and [SaveableStateProvider] to save and
 * restore state during configuration changes. The [contentKey] is used as a key for both
 * the composition key and the state save/restore mechanism.
 *
 * The [metadata] property can be used to store additional information about the destination,
 * such as pane configuration keys used by [Scene] implementations for multi-pane layouts.
 *
 * Example usage:
 * ```
 * @Parcelize
 * class SampleDestination : ComposeDestination() {
 *     override val content: @Composable () -> Unit = {
 *         SampleScreen() // composable function
 *     }
 * }
 * ```
 *
 * Note: [LocalSaveableStateHolder] must be provided in the composition hierarchy
 * for state saving to work properly.
 */

abstract class ComposeDestination(
    private val movableContentMap: MutableMap<Any, @Composable (@Composable () -> Unit) -> Unit> = mutableStateMapOf()
) : Destination {

    /**
     * The key used for composition identity and state save/restore.
     *
     * This key is used by [Content] to manage composition identity via [key] and to save/restore
     * state via [SaveableStateProvider]. By default, it uses [defaultContentKey] which converts
     * the destination instance to a string representation.
     *
     * You can override this property to provide a custom key, which is useful when you need
     * different instances of the same destination class to maintain separate state (e.g., viewing
     * different items by ID).
     */
    public open val contentKey: Any = defaultContentKey(this)

    /**
     * Additional metadata associated with this destination.
     *
     * This map can store arbitrary key-value pairs that provide additional information about
     * the destination. It is commonly used by [Scene] implementations to configure multi-pane
     * layouts, such as marking destinations as list panes or detail panes.
     *
     * By default, this is an empty map, but subclasses can override it to provide metadata
     * needed for specific navigation scenarios.
     */
    public open val metadata: Map<Any, Any> = emptyMap()

    /**
     * The composable content that defines the UI for this destination.
     *
     * Subclasses must provide an implementation of this property that returns the composable
     * screen to be displayed. This composable function does not take any parameters and should
     * render the complete UI for the destination.
     */
    protected abstract val content: @Composable () -> Unit

    /**
     * Composable function that renders the destination content with state management.
     *
     * This function orchestrates the rendering of the destination's [content] composable with
     * proper state management and composition optimization:
     *
     * - Uses [key] with [contentKey] to maintain composition identity across recompositions
     * - Uses [movableContentOf] to optimize recomposition when the destination moves within
     *   the composition tree (e.g., during navigation transitions)
     * - Uses [SaveableStateProvider] with [contentKey] to save and restore state during
     *   configuration changes (e.g., screen rotations)
     *
     * The [SaveableStateHolder] is obtained from [LocalSaveableStateHolder], which must be
     * provided in the composition hierarchy (typically via [Navigation] composable).
     *
     * This function should be called from navigation rendering code (such as [Pane]) rather than
     * directly in application code.
     */
    @Composable
    public fun Content() {
        val localSaveableStateHolder = LocalSaveableStateHolder.current

        key(contentKey) {
            val movableContent = remember {
                movableContentMap.getOrPut(contentKey) {
                    movableContentOf { content -> content() }
                }
            }

            movableContent {
                localSaveableStateHolder.SaveableStateProvider(contentKey) {
                    content()
                }
            }
        }
    }
}

/**
 * Converts the given object to a string representation to use as a default content key.
 *
 * This is used as the default implementation for [ComposeDestination.contentKey] when
 * no custom key is provided.
 */
@PublishedApi
internal fun defaultContentKey(key: Any): Any = key.toString()
