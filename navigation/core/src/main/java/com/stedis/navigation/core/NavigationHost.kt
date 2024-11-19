package com.stedis.navigation.core

import kotlin.reflect.KClass

data class NavigationHost(
    val hostName: String,
    val currentDestination: Destination,
    val store: List<Destination>,
)

fun NavigationHost(
    hostName: String,
    initialDestination: Destination,
    params: (NavigationHostBuilder.() -> Unit)? = null,
): NavigationHost =
    NavigationHostBuilder(hostName, initialDestination).also { if (params != null) it.params() }
        .build()

fun NavigationHost.buildNewHost(params: (NavigationHostBuilder.() -> Unit)? = null): NavigationHost =
    NavigationHostBuilder(hostName, store.first()).also {
        it.updateStore(store)
        if (params != null) it.params()
    }.build()

class NavigationHostBuilder(private val hostName: String, initialDestination: Destination) {

    private var _store = mutableListOf<Destination>(initialDestination)
    public val store: List<Destination>
        get() = _store

    private val currentDestination: Destination
        get() = _store.last()

    public fun updateStore(store: List<Destination>) {
        _store.clear()
        _store.addAll(store)
    }

    public fun addDestination(destination: Destination) {
        checkDestination(destination)

        _store.add(destination)
    }

    public fun replaceDestination(destination: Destination) {
        checkDestination(destination)

        _store = _store.dropLast(ONE).toMutableList()
        _store.add(destination)
    }

    public fun popDestination() {
        checkStoreSize()
        _store = _store.dropLast(ONE).toMutableList()
    }

    public fun popToDestination(destinationClass: KClass<out Destination>) {
        checkDestination(destinationClass)
        checkStoreSize()
        val index = _store.indexOfLast { it::class == destinationClass }
        if (index == NOT_FOUND_INDEX) throw Error("store does not contain destination ${destinationClass.simpleName}")

        _store = _store.dropLast(_store.size - index - ONE).toMutableList()
    }

    public fun popToDestination(destination: Destination) {
        checkDestination(destination)
        checkStoreSize()
        val index = if (_store.any { it == destination })
            store.indexOf(destination)
        else throw Error("store does not contain destination ${destination::class.simpleName}")

        _store = _store.dropLast(_store.size - index - ONE).toMutableList()
    }

    private fun checkStoreSize() {
        require(_store.size == ONE) {"store size cannot be less than 1"}
    }

    private fun checkDestination(destination: Destination) {
        val hostNames = destination::class.annotations.filterIsInstance<Host>()
        require(hostNames.isNotEmpty() && hostNames.none { it.hostName == hostName }){
            "destination: ${destination::class.simpleName} can't be add in host: $hostName"
        }
    }

    private fun checkDestination(destinationClass: KClass<out Destination>) {
        val hostNames = destinationClass.annotations.filterIsInstance<Host>()
        require(hostNames.isNotEmpty() && hostNames.none { it.hostName == hostName }){
            "destination: ${destinationClass.simpleName} can't be add in host: $hostName"
        }
    }

    public fun build(): NavigationHost =
        NavigationHost(
            hostName = hostName,
            currentDestination = currentDestination,
            store = store,
        )

    companion object {
        private const val ONE = 1
        private const val NOT_FOUND_INDEX = -1
    }
}