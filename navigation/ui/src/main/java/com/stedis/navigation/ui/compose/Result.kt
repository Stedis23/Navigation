package com.stedis.navigation.ui.compose

import android.os.Bundle

class ResultManager {
    private val resultsMap = mutableMapOf<String, Bundle>()
    private val listenersMap = mutableMapOf<String, ResultListener>()

    fun sendResult(key: String, result: Bundle) {
        resultsMap[key] = result
        listenersMap[key]?.onResult(result)
    }

    fun setResultListener(key: String, body: (Bundle) -> Unit) =
        object : ResultListener {
            override fun onResult(result: Bundle) {
                body(result)
            }
        }.also {
            listenersMap[key] = it
            resultsMap[key]?.let { result -> it.onResult(result) }
        }

    fun registerListener(key: String, listener: ResultListener) {
        listenersMap[key] = listener

        resultsMap[key]?.let { listener.onResult(it) }
    }

    fun unregisterListener(key: String) {
        listenersMap.remove(key)
    }
}

interface ResultListener {
    fun onResult(result: Bundle)
}