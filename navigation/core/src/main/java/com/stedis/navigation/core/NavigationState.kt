package com.stedis.navigation.core

/**
 * [NavigationState] represents the global navigation state that holds all information related to navigation.
 *
 * @property hosts A list of all navigation hosts in the current state.
 * @property currentHost The currently active navigation host.
 * @property currentDestination The currently active destination within the current host.
 */
data class NavigationState(
    val hosts: List<NavigationHost>,
    val currentHost: NavigationHost,
    val currentDestination: Destination,
)

/**
 * Creates a new instance of [NavigationState].
 *
 * Example of using `NavigationState`:
 * ```
 * val navigationState = NavigationState(initialHost = mainHost)){
 *     Host(articlesHost)
 *     Host(musicHost)
 * }
 *```
 *
 * @param initialHost The initial navigation host to set.
 * @param params An optional lambda function to configure the [NavigationStateBuilder].
 *
 * @return A new instance of [NavigationState].
 */
public fun NavigationState(
    initialHost: NavigationHost,
    params: (NavigationStateBuilder.() -> Unit)? = null
): NavigationState =
    NavigationStateBuilder(initialHost).also { if (params != null) it.params() }.build()

/**
 * Creates a new instance of [NavigationState], changing only the current host.
 *
 * @param params An optional lambda function to configure the [NavigationHostBuilder].
 *
 * @return A new instance of [NavigationState] with updated hosts.
 */
public fun NavigationState.buildNewStateWithCurrentHost(params: (NavigationHostBuilder.() -> Unit)? = null): NavigationState =
    buildNewState {
        updateHosts {
            this@buildNewState.hosts.map { host ->
                if (host.hostName == this@buildNewState.currentHost.hostName) {
                    host.buildNewHost(params)
                } else {
                    host
                }
            }
        }
    }

/**
 * Builds a new [NavigationState] instance based on the current state.
 *
 * @param params An optional lambda function to configure the [NavigationStateBuilder].
 *
 * @return A new instance of [NavigationState].
 */
public fun NavigationState.buildNewState(params: (NavigationStateBuilder.() -> Unit)? = null): NavigationState =
    NavigationStateBuilder(currentHost).also {
        it.updateHosts { this@buildNewState.hosts }
        if (params != null) it.params()
    }.build()

/**
 * Searches for a [NavigationHost] by its host name within the list of available hosts.
 *
 * @param hostName The name of the host to search for.
 *
 * @return The [NavigationHost] instance that matches the specified [hostName],
 *         or null if no such host exists.
 */
public fun NavigationState.findHost(hostName: String): NavigationHost? =
    hosts.find { it.hostName == hostName }

/**
 * Builder class for creating and modifying instances of [NavigationState].
 *
 * @property initialHost The initial navigation host to start with.
 */
class NavigationStateBuilder(initialHost: NavigationHost) {

    /**
     * A mutable list of navigation hosts.
     */
    public var hosts: MutableList<NavigationHost> = mutableListOf(initialHost)
        private set

    /**
     * The currently active navigation host.
     */
    public var currentHost: NavigationHost = initialHost
        private set

    private val currentDestination: Destination
        get() = currentHost.currentDestination

    /**
     * Updates the list of navigation hosts based on the provided lambda function.
     *
     * @param body A lambda function that returns a new list of navigation hosts.
     *
     * @return The current instance of [NavigationStateBuilder].
     */
    public fun updateHosts(body: NavigationStateBuilder.() -> List<NavigationHost>) =
        apply {
            val newHosts = body()
            hosts = newHosts.toMutableList()
            updateCurrentHost(currentHost.hostName, hosts)
        }

    /**
     * Sets the current host based on the provided host name.
     *
     * @param hostName The name of the host to set as current.
     *
     * @return The current instance of [NavigationStateBuilder].
     */
    public fun setCurrentHost(hostName: String) =
        apply {
            updateCurrentHost(hostName, hosts)
        }

    private fun updateCurrentHost(hostName: String, hosts: List<NavigationHost>) {
        currentHost = hosts.find { it.hostName == hostName }
            ?: throw error("state does not contain host: $hostName")
    }

    /**
     * Adds a new host to the navigation state.
     *
     * @param hostName The name of the host to add.
     * @param initialDestination The initial destination for the new host.
     * @param body An optional lambda function to configure the [NavigationHostBuilder].
     *
     * @return The current instance of [NavigationStateBuilder].
     */
    @Suppress("FunctionName")
    public fun Host(
        hostName: String,
        initialDestination: Destination,
        body: (NavigationHostBuilder.() -> Unit)? = null
    ) =
        apply {
            hosts.forEach {
                if (it.hostName == hostName) throw error("Multiple hosts have name: $hostName, hostName must be unique.")
            }
            hosts += NavigationHost(hostName, initialDestination, body)
        }

    /**
     * Removes a [NavigationHost] from the list of hosts by its host name.
     *
     * @param hostName The name of the host to be removed.
     *
     * @return The current instance of [NavigationStateBuilder].
     */
    public fun removeHost(hostName: String) =
        apply {
            hosts.removeIf { it.hostName == hostName }
        }

    /**
     * Builds a new instance of [NavigationState] using the current state of the builder.
     *
     * @return A new instance of [NavigationState].
     */
    public fun build(): NavigationState =
        NavigationState(
            hosts = hosts,
            currentHost = currentHost,
            currentDestination = currentDestination,
        )
}