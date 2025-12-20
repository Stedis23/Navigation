package com.stedis.navigation.compose

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.scenes.SinglePaneSceneStrategy
import com.stedis.navigation.core.NavigationHost

/**
 * A representation of a UI screen or a distinct part of the UI in a Compose-based application.
 *
 * A Scene encapsulates the Composable content that should be displayed. It is the output of a
 * [SceneStrategy], which determines the appropriate scene based on the current navigation state
 * and device configuration (e.g., screen size, orientation).
 *
 * @property composable The Composable lambda that defines the UI content for this scene.
 *                      It receives the [NavigationHost] to allow for nested navigation and
 *                      state access within the scene.
 */
public interface Scene {

    public val composable: @Composable (NavigationHost) -> Unit
}

/**
 * A strategy pattern interface for determining which [Scene] to display based on the current state.
 *
 * Implementations of this interface contain the logic to decide the UI layout. Common strategies include:
 * - [SinglePaneSceneStrategy]: Always shows a single pane (e.g., for phones).
 * - [TwoPaneSceneStrategy]: Shows a master-detail view (e.g., for tablets in landscape).
 * - [ConditionalStrategy]: Chooses a scene based on dynamic conditions like feature flags or user roles.
 *
 * The navigation system evaluates a list of strategies in order until one returns a non-null scene.
 */
public interface SceneStrategy {

    @Composable
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
 * Example: `val strategy = SinglePaneStrategy then TwoPaneStrategy`
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
 * @param navigationHost The root navigation controller and state holder.
 * @param sceneStrategy The strategy used to determine the scene to display. Defaults to
 *                      [SinglePaneSceneStrategy].
 */
@Composable
public fun Scene(
    navigationHost: NavigationHost,
    sceneStrategy: SceneStrategy = SinglePaneSceneStrategy,
) {
    val scene =
        sceneStrategy.calculateScene(navigationHost) ?: SinglePaneSceneStrategy.calculateScene(
            navigationHost
        )

    scene.composable(navigationHost)
}
