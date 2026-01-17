package com.stedis.navigation.core

import kotlinx.coroutines.cancel

public typealias StateBuilderDeclaration = NavigationStateBuilder.() -> Unit

/**
 * [NavigationState] represents the global navigation state that holds all information related to navigation.
 *
 * @property hosts A list of all navigation hosts in the current state.
 * @property currentHost The currently active navigation host.
 * @property currentDestination The currently active destination within the current host.
 */
public data class NavigationState(
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
@NavigationDslMarker
public fun NavigationState(
    initialHost: NavigationHost,
    params: StateBuilderDeclaration? = null
): NavigationState =
    NavigationStateBuilder(initialHost)
        .also { if (params != null) it.params() }
        .build()

/**
 * Creates a new instance of [NavigationState], changing only the current root host.
 *
 * @param params An optional lambda function to configure the [NavigationHostBuilder].
 *
 * @return A new instance of [NavigationState] with updated hosts.
 */
@NavigationDslMarker
public fun NavigationState.buildNewStateWithCurrentHost(params: HostBuilderDeclaration? = null): NavigationState =
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
@NavigationDslMarker
public fun NavigationState.buildNewState(params: StateBuilderDeclaration? = null): NavigationState =
    NavigationStateBuilder(currentHost)
        .updateHosts { this@buildNewState.hosts }
        .setScopeDestinations()
        .also { if (params != null) it.params() }
        .cancelDeadScopes()
        .build()

/**
 * Builder class for creating and modifying instances of [NavigationState].
 *
 * @property initialHost The initial navigation host to start with.
 */
class NavigationStateBuilder(initialHost: NavigationHost) {

    private var _hosts: MutableList<NavigationHost> = mutableListOf(initialHost)
        set(value) {

            field = value
            updateTraversalContext()
        }

    /**
     * A mutable list of navigation hosts.
     */
    public val hosts: List<NavigationHost>
        get() = _hosts

    /**
     * The currently active navigation host.
     */
    public var currentHost: NavigationHost = initialHost
        private set

    private val currentDestination: Destination
        get() = currentHost.currentDestination

    /**
     * The currently traversal context.
     */
    public var traversalContext: TraversalContext = emptyTraversalContext().copy(hosts = _hosts)
        private set

    private var scopeDestinations: List<ScopeDestination> = findScopeDestinations(_hosts)

    private fun findScopeDestinations(hosts: List<NavigationHost>): List<ScopeDestination> {
        val scopeDestinations = mutableListOf<ScopeDestination>()
        traverseHosts(hosts, scopeDestinations)
        return scopeDestinations
    }

    private fun traverseHosts(
        hosts: List<NavigationHost>,
        scopeDestinations: MutableList<ScopeDestination>
    ) {
        hosts.forEach { host ->
            host.stack.forEach { destination ->
                if (destination is ScopeDestination) {
                    scopeDestinations.add(destination)
                }
            }

            traverseHosts(host.children, scopeDestinations)
        }
    }

    internal fun cancelDeadScopes() =
        apply {
            val newScopeDestinations = findScopeDestinations(_hosts)
            cancelDeadScopes(newScopeDestinations, scopeDestinations)
        }

    private fun cancelDeadScopes(
        currentScopeDestinations: List<ScopeDestination>,
        previousScopeDestinations: List<ScopeDestination>
    ) {
        val toCancel = previousScopeDestinations.filterNot { previous ->
            currentScopeDestinations.any { current -> current == previous }
        }

        toCancel.forEach { it.destinationScope.cancel() }
    }

    private fun updateTraversalContext() {
        traversalContext = TraversalContext(
            hosts = _hosts,
            points = traversalContext.points
        )
    }

    internal fun setScopeDestinations() =
        apply {
            scopeDestinations = findScopeDestinations(_hosts)
        }

    /**
     * Updates the list of navigation hosts based on the provided lambda function.
     *
     * @param body A lambda function that returns a new list of navigation hosts.
     *
     * @return The current instance of [NavigationStateBuilder].
     */
    @NavigationDslMarker
    public fun updateHosts(body: NavigationStateBuilder.() -> List<NavigationHost>) =
        apply {
            val newHosts = body()
            _hosts = newHosts.toMutableList()
            updateCurrentHost(currentHost.hostName, _hosts)
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
            updateCurrentHost(hostName, _hosts)
        }

    private fun updateCurrentHost(hostName: String, hosts: List<NavigationHost>) {
        currentHost = hosts.find { it.hostName == hostName }
            ?: throw IllegalArgumentException("state does not contain host: $hostName")
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
        body: HostBuilderDeclaration? = null
    ) =
        apply {
            _hosts.forEach {
                require(it.hostName != hostName) {
                    "Multiple hosts have name: $hostName, hostName must be unique."
                }
            }
            _hosts += NavigationHost(hostName, initialDestination, body)
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
            _hosts.removeIf { it.hostName == hostName }
        }

    /**
     * Creates a new [TraversalContext] with the current instance of [NavigationStateBuilder] and a specified host name.
     *
     * This infix function adds the specified host name as the first point in the traversal context.
     *
     * @param hostName The name of the host to be added to the traversal context.
     *
     * @return A new [TraversalContext] instance containing the current hosts and the specified host name as the first point.
     */
    public infix fun inside(hostName: String): TraversalContext =
        TraversalContext(
            hosts = _hosts,
            points = listOf(hostName),
        )

    /**
     * Performs a navigation operation based on the current [TraversalContext] and a provided
     * configuration block for a [NavigationHostBuilder].
     *
     * This infix function finds the shortest path in the host tree using the BFS algorithm
     * based on the hosts and points in the current traversal context. If the path is found,
     * it modifies the last host on the way using the provided configuration block and returns the updated
     * [NavigationStateBuilder].
     *
     * Example of using perform:
     * ```
     * traversalContext
     *  .inside("firstHost")
     *      .inside("secondHost")
     *      .perform {
     *          addDestination(NewDestination)
     *      }
     * ```
     *
     * @param body A lambda with receiver of type [NavigationHostBuilder] that allows for
     *              configuring the navigation hosts.
     *
     * @return The updated [NavigationStateBuilder] instance with modified hosts.
     *
     * @throws IllegalArgumentException If the shortest path cannot be found in the host tree.
     */
    @NavigationDslMarker
    public infix fun TraversalContext.perform(body: HostBuilderDeclaration): NavigationStateBuilder {
        val path: List<String> = findShortestPathBFS(hosts, points)
            ?: throw IllegalArgumentException("The given path was not found in the host tree")
        updateHosts { modifyHost(_hosts, path, body).toMutableList() }

        return this@NavigationStateBuilder
    }

    /**
     * Switches the current navigation context to the host specified by the [hostName] parameter
     * based on the shortest path derived from the current [TraversalContext].
     *
     * This function finds the shortest path in the host tree using the BFS algorithm,
     * starting from the hosts and points in the current traversal context and targeting
     * the specified [hostName]. It updates the hosts in the [NavigationStateBuilder]
     * according to the first host in the path. If the path consists of only one host,
     * it remains unchanged; otherwise, the child host corresponding to the next point
     * in the path is selected.
     *
     * Example of using switch with hostName:
     * ```
     * traversalContext
     *  .inside("firstHost")
     *      .inside("secondHost")
     *      .switch("targetHost")
     * ```
     *
     * @param hostName The name of the host to switch to.
     * @return The updated [NavigationStateBuilder] instance with the modified hosts.
     *
     * @throws IllegalStateException If the path is empty.
     * @throws IllegalArgumentException If the shortest path cannot be found in the host tree,
     *                                  if the root host cannot be determined,
     *                                  or if an invalid path segment is encountered during update.
     */
    public fun TraversalContext.switch(hostName: String): NavigationStateBuilder {
        val path: List<String> = findShortestPathBFS(hosts, points + hostName)
            ?: throw IllegalArgumentException("The given path was not found in the host tree")

        if (path.isEmpty()) throw IllegalStateException("Path cannot be empty")

        val head = path.first()
        val root = _hosts.find { it.hostName == head }
            ?: throw IllegalArgumentException("Root host cannot be null")

        val updatedRoot = updateHostAlongPath(root, path.drop(1))

        this@NavigationStateBuilder._hosts = hosts.map {
            if (it.hostName == head) updatedRoot else it
        }.toMutableList()

        setCurrentHost(head)
        return this@NavigationStateBuilder
    }

    private fun updateHostAlongPath(
        host: NavigationHost,
        remainingPath: List<String>
    ): NavigationHost {
        if (remainingPath.isEmpty()) return host

        val stack = mutableListOf<Pair<NavigationHost, Int>>()
        var currentHost = host
        var currentPath = remainingPath

        while (currentPath.isNotEmpty()) {
            val nextHostName = currentPath.first()
            val nextIndex = currentHost.children.indexOfFirst { it.hostName == nextHostName }
            if (nextIndex == -1) throw IllegalArgumentException("Invalid path: $nextHostName not found")

            stack.add(Pair(currentHost, nextIndex))
            currentHost = currentHost.children[nextIndex]
            currentPath = currentPath.drop(1)
        }

        var newHost = currentHost
        for ((parentHost, childIndex) in stack.asReversed()) {
            val newChildren = parentHost.children.toMutableList()
            newChildren[childIndex] = newHost

            newHost = parentHost.copy(
                children = newChildren,
                selectedChild = newChildren[childIndex]
            )
        }

        return newHost
    }

    /**
     * Builds a new instance of [NavigationState] using the current state of the builder.
     *
     * @return A new instance of [NavigationState].
     */
    public fun build(): NavigationState =
        NavigationState(
            hosts = _hosts,
            currentHost = currentHost,
            currentDestination = currentDestination,
        )
}