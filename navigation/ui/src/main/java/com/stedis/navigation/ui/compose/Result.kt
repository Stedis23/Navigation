package com.stedis.navigation.ui.compose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable

@SuppressLint("ComposableNaming")
@Composable
public fun setResultListener(key: String, body: (Bundle) -> Unit) {
    val navigationViewModel = getNavigationViewModel()
    navigationViewModel.resultManager.setResultListener(key, body)
}

@SuppressLint("ComposableNaming")
@Composable
public fun sendResult(key: String, result: Bundle) {
    val navigationViewModel = getNavigationViewModel()
    navigationViewModel.resultManager.sendResult(key, result)
}

class ResultManager {
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