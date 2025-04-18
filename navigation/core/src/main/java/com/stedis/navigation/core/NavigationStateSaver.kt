package com.stedis.navigation.core

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

private const val HOSTS_NAMES = "stedis:navigation:saver:hosts_names"
private const val CURRENT_HOST_NAME = "stedis:navigation:saver:current_host_name"
private const val HOST_STORE = "stedis:navigation:saver:host:"

public fun NavigationManager.saveState(): Bundle? {
    var bundle: Bundle? = null

    if (this.currentState.hosts.isNotEmpty()) {
        bundle = Bundle()
        val hosts = ArrayList<String>()
        this.currentState.hosts.forEach {
            hosts.add(it.hostName)
            val store = arrayListOf<Parcelable>()
            it.stack.forEach { destination ->
                store.add(destination as Parcelable)
            }

            bundle.putParcelableArrayList(HOST_STORE + it.hostName, store)
        }

        bundle.putStringArrayList(HOSTS_NAMES, hosts)
        bundle.putString(CURRENT_HOST_NAME, this.currentState.currentHost.hostName)
    }

    return bundle
}

public fun restoreState(bundle: Bundle?): NavigationState {
    if (bundle == null) {
        throw error("state don't was saved")
    }

    val hosts = mutableListOf<NavigationHost>()
    bundle.getStringArrayList(HOSTS_NAMES)?.forEach {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val store = bundle.getParcelableArrayList(HOST_STORE + it, Destination::class.java)
                ?: throw error("store don't was saved")

            hosts.add(
                NavigationHost(
                    hostName = it,
                    currentDestination = store.last(),
                    stack = store
                )
            )
        }
    }

    val currentHostName = bundle.getString(CURRENT_HOST_NAME)
    val currentHost =
        hosts.find { it.hostName == currentHostName } ?: throw error("current host cant be empty")

    return NavigationState(
        hosts = hosts,
        currentHost = currentHost,
        currentDestination = currentHost.currentDestination
    )
}