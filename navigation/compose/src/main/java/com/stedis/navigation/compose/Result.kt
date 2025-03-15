package com.stedis.navigation.compose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import org.jetbrains.annotations.ApiStatus.Experimental

/**
 * A set of composable functions for managing results in a navigation context.
 * These functions allow for the registration, sending, and handling of results
 * associated with specific keys, enabling communication between different
 * composables within the navigation framework.
 *
 * Each function interacts with the [ResultManager] to facilitate the
 * registration of listeners and the sending of results, ensuring that
 * results can be properly received and processed by the registered listeners.
 */

/**
 * Sets a result listener for a specified key. When a result is sent for the given key,
 * the provided callback will be invoked with the result.
 *
 * @param key A unique identifier for the result listener.
 * @param body A lambda function that will be invoked with the result as a [Bundle]
 * when the result is sent for the specified key.
 *
 * @throws IllegalStateException If the navigation ViewModel is not available.
 */
@Experimental
@SuppressLint("ComposableNaming")
@Composable
public fun setResultListener(key: String, body: (Bundle) -> Unit) {
    val navigationViewModel = getNavigationViewModel()
    navigationViewModel.resultManager.setResultListener(key, body)
}

/**
 * Sends a result associated with a specified key. All listeners registered for this key
 * will be notified with the provided result.
 *
 * @param key A unique identifier for the result.
 * @param result A [Bundle] containing the result data to be sent to listeners.
 *
 * @throws IllegalStateException If the navigation ViewModel is not available.
 */
@Experimental
@SuppressLint("ComposableNaming")
@Composable
public fun sendResult(key: String, result: Bundle) {
    val navigationViewModel = getNavigationViewModel()
    navigationViewModel.resultManager.sendResult(key, result)
}

/**
 * Registers a result listener for a specified key. The listener will be notified
 * when results are sent for this key.
 *
 * @param key A unique identifier for the result listener.
 * @param listener An instance of [ResultListener] that will handle the results
 * sent for the specified key.
 *
 * @throws IllegalStateException If the navigation ViewModel is not available.
 */
@Experimental
@SuppressLint("ComposableNaming")
@Composable
public fun registerListener(key: String, listener: ResultListener) {
    val navigationViewModel = getNavigationViewModel()
    navigationViewModel.resultManager.registerListener(key, listener)
}

/**
 * Unregisters the result listener associated with the specified key. Once unregistered,
 * the listener will no longer receive results.
 *
 * @param key A unique identifier for the result listener to be unregistered.
 *
 * @throws IllegalStateException If the navigation ViewModel is not available.
 */
@Experimental
@SuppressLint("ComposableNaming")
@Composable
public fun unregisterListener(key: String) {
    val navigationViewModel = getNavigationViewModel()
    navigationViewModel.resultManager.unregisterListener(key)
}

@PublishedApi
internal class ResultManager {
    private val resultsMap = mutableMapOf<String, Bundle>()
    private val listenersMap = mutableMapOf<String, ResultListener>()

    public fun sendResult(key: String, result: Bundle) {
        resultsMap[key] = result
        listenersMap[key]?.onResult(result)
    }

    public fun setResultListener(key: String, body: (Bundle) -> Unit) =
        object : ResultListener {
            override fun onResult(result: Bundle) {
                body(result)
            }
        }.also {
            listenersMap[key] = it
            resultsMap[key]?.let { result -> it.onResult(result) }
        }

    public fun registerListener(key: String, listener: ResultListener) {
        listenersMap[key] = listener

        resultsMap[key]?.let { listener.onResult(it) }
    }

    public fun unregisterListener(key: String) {
        listenersMap.remove(key)
    }
}

interface ResultListener {
    public fun onResult(result: Bundle)
}