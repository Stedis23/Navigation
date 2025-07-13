package com.stedis.navigation.core

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

private const val HOSTS = "stedis:navigation:saver:hosts"
private const val CURRENT_HOST_NAME = "stedis:navigation:saver:current_host_name"

public fun NavigationManager.saveState(): Bundle? {
    var bundle: Bundle? = null

    if (this.currentState.hosts.isNotEmpty()) {
        bundle = Bundle()
        val hosts = arrayListOf<Parcelable>()
        this.currentState.hosts.forEach {
            hosts.add(it as Parcelable)
        }

        bundle.putParcelableArrayList(HOSTS, hosts)
        bundle.putString(CURRENT_HOST_NAME, this.currentState.currentHost.hostName)
    }

    return bundle
}

public fun restoreState(bundle: Bundle?): NavigationState {
    require(bundle != null) { "state don't was saved" }

    var hosts = mutableListOf<NavigationHost>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hosts = bundle.getParcelableArrayList(HOSTS, NavigationHost::class.java)
            ?: throw IllegalStateException("store don't was saved")
    }

    val currentHostName = bundle.getString(CURRENT_HOST_NAME)
    val currentHost =
        hosts.find { it.hostName == currentHostName }
            ?: throw IllegalArgumentException("current host cant be empty")

    return NavigationState(
        hosts = hosts,
        currentHost = currentHost,
        currentDestination = currentHost.currentDestination
    )
}