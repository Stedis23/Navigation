package com.stedis.navigation.core

data class NavigationHost(
    val hostName: String,
    val currentDestination: Destination,
    val store: List<Destination>,
)

fun NavigationHost(
    hostName: String,
    initialDestination: Destination,
    params: (NavigationHostBuilder.() -> Unit)? = null,
): NavigationHost = NavigationHostBuilder(hostName, initialDestination).also { if (params != null) it.params() }.build()

fun NavigationHost.buildNewHost(params: (NavigationHostBuilder.() -> Unit)? = null): NavigationHost =
    NavigationHostBuilder(hostName, store.first()).also {
        it.updateStore(store)
        if (params != null) it.params()
    }.build()

class NavigationHostBuilder(private val hostName: String, initialDestination: Destination) {


    private val _store = mutableListOf<Destination>(initialDestination)
    public val store: List<Destination> = _store

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

        _store.removeLast()
        _store.add(destination)
    }

    public fun popDestination() {
        if (_store.size == ONE) throw error("store size cannot be less than 1")

        _store.removeLast()
    }

    public fun popToDestination(destination: Destination) {
        checkDestination(destination)

        if (_store.size == ONE) throw error("store size cannot be less than 1")

        val index = if (_store.any { it == destination })
            store.indexOf(destination)
        else throw Error("store does not contain destination ${destination::class.simpleName}")

        _store.dropLast(_store.size - index)
    }

    private fun checkDestination(destination: Destination) {
        val hostNames = destination::class.annotations.filterIsInstance<Host>()
        if (hostNames.isNotEmpty() && hostNames.none { it.hostName == hostName })
            throw error("destination: ${destination::class.simpleName} can't be add in host: $hostName")
    }

    public fun build(): NavigationHost =
        NavigationHost(
            hostName = hostName,
            currentDestination = currentDestination,
            store = store,
        )

    companion object {
        private const val ONE = 1
    }
}