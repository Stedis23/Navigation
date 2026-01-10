package com.stedis.navigation.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import com.stedis.navigation.compose.scenes.SinglePaneScene
import com.stedis.navigation.compose.scenes.SinglePaneSceneStrategy
import com.stedis.navigation.core.NavigationHost

/**
 * A representation of a UI screen or a distinct part of the UI in a Compose-based application.
 *
 * A Scene encapsulates the Composable content that should be displayed. It is the output of a
 * [SceneStrategy], which determines the appropriate scene based on the current navigation state
 * and device configuration (e.g., screen size, orientation).
 *
 * @property key A unique identifier for this scene instance.
 * @property content The Composable lambda that defines the UI content for this scene.
 * @property destinations The list of [ComposeDestination] instances that are part of this scene.
 * @property metadata A map containing metadata associated with this scene, typically derived
 *                   from the last destination's metadata.
 */
public interface Scene {

    public val key: Any

    public val content: @Composable () -> Unit

    public val destinations: List<ComposeDestination>

    public val metadata: Map<Any, Any>
        get() = destinations.lastOrNull()?.metadata ?: emptyMap()
}

/**
 * A strategy pattern interface for determining which [Scene] to display based on the current state.
 *
 * Implementations of this interface contain the logic to decide the UI layout. The most common
 * strategy is [SinglePaneSceneStrategy], which always shows a single pane (e.g., for phones).
 *
 * Custom strategies can be implemented to support adaptive layouts based on device configuration,
 * screen size, orientation, or other dynamic conditions.
 *
 * The navigation system evaluates a list of strategies in order until one returns a non-null scene.
 *
 * @param navigationHost The navigation host containing the current navigation state.
 * @return The calculated [Scene] to display, or `null` if this strategy is not applicable.
 */
public interface SceneStrategy {

    public fun calculateScene(navigationHost: NavigationHost): Scene?
}

public infix operator fun SceneStrategy.plus(otherSceneStrategy: SceneStrategy): List<SceneStrategy> =
    listOf(this, otherSceneStrategy)

/**
 * Creates a sequential chain of two [SceneStrategy] objects.
 *
 * This infix function allows for a clean, declarative DSL to build lists of strategies.
 * The system will evaluate `this` strategy first, and if it returns `null`, it will
 * evaluate the `otherSceneStrategy`.
 *
 * Example: `val strategy = customStrategy then SinglePaneSceneStrategy`
 *
 * @param otherSceneStrategy The next strategy to try if the current one is not applicable.
 * @return A [List] containing the two strategies in evaluation order.
 */
public infix fun SceneStrategy.then(otherSceneStrategy: SceneStrategy): List<SceneStrategy> =
    listOf(this, otherSceneStrategy)

/**
 * Appends a [SceneStrategy] to an existing list of strategies.
 *
 * This allows for extending a previously created chain of strategies.
 *
 * Example: `val complexStrategy = (strategyA then strategyB) then strategyC`
 *
 * @param otherSceneStrategy The strategy to append to the list.
 * @return A new [List] containing all strategies from the original list plus the new one.
 */
public infix fun List<SceneStrategy>.then(otherSceneStrategy: SceneStrategy): List<SceneStrategy> =
    this + otherSceneStrategy

/**
 * The core Composable that resolves and displays the correct UI scene based on a prioritized list of strategies.
 *
 * This function iterates through the provided [sceneStrategies] in order, using the first one that
 * returns a non-null [Scene]. If no strategy in the list provides a scene, it falls back to the
 * [SinglePaneSceneStrategy] as a default.
 *
 * This is the primary entry point for a navigation host that supports adaptive layouts.
 *
 * @param navigationHost The root navigation controller and state holder.
 * @param sceneStrategies An ordered list of strategies to evaluate for choosing the scene.
 *                        Strategies are evaluated from first to last.
 */
@Composable
public fun Scene(
    navigationHost: NavigationHost,
    sceneStrategies: List<SceneStrategy>,
) {
    var targetStrategy: SceneStrategy? = null
    for (strategy in sceneStrategies) {
        val scene = strategy.calculateScene(navigationHost)
        if (scene != null) {
            targetStrategy = strategy
            break
        }
    }

    Scene(
        navigationHost,
        targetStrategy ?: SinglePaneSceneStrategy,
    )
}

/**
 * A convenience Composable that displays a UI scene based on a single strategy.
 *
 * If the provided strategy cannot calculate a scene (returns `null`), it falls back to
 * the [SinglePaneSceneStrategy]. This ensures that the UI always has something to display.
 *
 * The scene transitions are animated based on the type of navigation change:
 * - If the scene class changes, [sceneTransitionAnimations] are used.
 * - Otherwise, animations from the scene's metadata or [navigationAnimations] are used.
 *
 * @param navigationHost The root navigation controller and state holder.
 * @param sceneStrategy The strategy used to determine the scene to display. Defaults to
 *                      [SinglePaneSceneStrategy].
 * @param navigationAnimations The default animations to use for navigation transitions within
 *                             the same scene type. Defaults to [getDefaultNavigationAnimations].
 * @param sceneTransitionAnimations The animations to use when transitioning between different
 *                                  scene types. Defaults to [getDefaultNavigationAnimations].
 * @param contentAlignment The alignment of the scene content within the AnimatedContent container.
 *                         Defaults to [Alignment.TopStart].
 * @param modifier The modifier to be applied to the AnimatedContent container.
 */
@Composable
public fun Scene(
    navigationHost: NavigationHost,
    sceneStrategy: SceneStrategy = SinglePaneSceneStrategy,
    navigationAnimations: NavigationAnimations = getDefaultNavigationAnimations(),
    sceneTransitionAnimations: NavigationAnimations = getDefaultNavigationAnimations(),
    contentAlignment: Alignment = Alignment.TopStart,
    modifier: Modifier = Modifier,
) {
    var previousHost by remember { mutableStateOf(navigationHost) }

    LaunchedEffect(navigationHost) {
        previousHost = navigationHost
    }

    val scene = remember(navigationHost) {
        sceneStrategy.calculateScene(navigationHost)
            ?: SinglePaneSceneStrategy.calculateScene(navigationHost)
    }

    var previousScene by remember { mutableStateOf(scene) }

    LaunchedEffect(scene) {
        previousScene = scene
    }

    val animations =
        if (scene::class != previousScene::class) sceneTransitionAnimations
        else scene.contentAnimations() ?: navigationAnimations

    val sceneToExcludedEntryMap = remember(scene) {
        buildMap {
            val scenesInZOrder = listOf(scene, previousScene)
            val coveredEntryKeys = mutableSetOf<Any>()

            if (
                scene::class to scene.key != previousScene::class to previousScene.key &&
                !(scene is SinglePaneScene && previousScene is SinglePaneScene)
            ) {
                scenesInZOrder.fastForEach { scene ->
                    val newlyCoveredEntryKeys =
                        scene.destinations
                            .map { it.contentKey }
                            .filterNot(coveredEntryKeys::contains)
                            .toSet()
                    put(scene::class to scene.key, coveredEntryKeys.toMutableSet())
                    coveredEntryKeys.addAll(newlyCoveredEntryKeys)
                }
            }
        }
    }

    val transitionSpec = getTransitionSpec(
        previousHost,
        navigationHost,
        animations,
    )

    AnimatedContent(
        targetState = scene,
        label = "scene_transition",
        contentKey = { it::class to it.key },
        transitionSpec = { transitionSpec.first using transitionSpec.second },
        contentAlignment = contentAlignment,
        modifier = modifier,
    ) { targetScene ->
        CompositionLocalProvider(
            LocalNavigationHost provides navigationHost,
            LocalEntriesToExcludeFromCurrentScene provides sceneToExcludedEntryMap.getOrDefault(
                targetScene::class to targetScene.key,
                HashSet(),
            ),
        ) {
            targetScene.content()
        }
    }
}

/**
 * Retrieves the custom navigation animations from the scene's metadata, if available.
 *
 * @return The [NavigationAnimations] from metadata, or `null` if not specified.
 */
private fun Scene.contentAnimations(): NavigationAnimations? {
    return metadata[ANIMATIONS_METADATA_KEY] as? NavigationAnimations
}