package com.stedis.navigation.core

data class NavigationState(
    val hosts: List<NavigationHost>,
    val currentHost: NavigationHost,
    val currentDestination: Destination,
)

public fun NavigationState(initialHost: NavigationHost, params: (NavigationStateBuilder.() -> Unit)? = null): NavigationState =
    NavigationStateBuilder(initialHost).also { if (params != null) it.params() }.build()

public fun NavigationState.buildNewStateWithCurrentHost(params: (NavigationHostBuilder.() -> Unit)? = null): NavigationState =
    buildNewState {
        updateHosts {
            hosts.map { host ->
                if (host.hostName == currentHost.hostName) {
                    host.buildNewHost(params)
                } else {
                    host
                }
            }
        }
    }

public fun NavigationState.buildNewState(params: (NavigationStateBuilder.() -> Unit)? = null): NavigationState =
    NavigationStateBuilder(hosts.first()).also {
        it.updateHosts { hosts }
        if (params != null) it.params()
    }.build()

class NavigationStateBuilder(initialHost: NavigationHost) {

    public var hosts: MutableList<NavigationHost> = mutableListOf(initialHost)
        private set

    public var currentHost: NavigationHost = initialHost
        private set

    private val currentDestination: Destination
        get() = currentHost.currentDestination

    public fun updateHosts(body: NavigationStateBuilder.() -> List<NavigationHost>) {
        val newHosts = body()
        updateCurrentHost(currentHost.hostName, newHosts)
        this.hosts.clear()
        this.hosts.addAll(newHosts)
    }

    public fun setCurrentHost(hostName: String) {
        updateCurrentHost(hostName, hosts)
    }

    private fun updateCurrentHost(hostName: String, hosts: List<NavigationHost>) {
        currentHost = hosts.find { it.hostName == hostName } ?: throw error("state does not contain host: $hostName")
    }

    public fun Host(hostName: String, initialDestination: Destination, body: (NavigationHostBuilder.() -> Unit)? = null) {
        hosts.forEach {
            require(it.hostName == hostName) { "Multiple hosts have name: $hostName, hostName must be unique."}
        }
        hosts += NavigationHost(hostName, initialDestination, body)
    }

    public fun build(): NavigationState =
        NavigationState(
            hosts = hosts,
            currentHost = currentHost,
            currentDestination = currentDestination,
        )
}