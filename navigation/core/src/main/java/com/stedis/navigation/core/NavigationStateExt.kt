package com.stedis.navigation.core

private const val ONE = 1

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
 * Retrieves the traversal context for a specific navigation host by its name,
 * starting the search from a specific traversal context.
 *
 * This method is useful when there are multiple [NavigationHost] instances with the same name
 * in the navigation state hierarchy. By providing a [startContext], you can specify the starting
 * point for the search, allowing the system to distinguish between different instances of hosts
 * with identical names by narrowing the search scope to a specific branch.
 *
 * The search begins from the host specified by the [startContext]'s points. The points from
 * [startContext] are used to find the starting host within the current navigation state's hosts.
 * If the [startContext] has points, the search starts from the host reached by following that path
 * in the current state. If [startContext] has no points, the search starts from all root hosts
 * in the current navigation state.
 *
 * The search is performed using an iterative depth-first traversal approach, examining each host
 * and its children until a host with a matching [targetName] is found or all possibilities
 * are exhausted.
 *
 * @param targetName The name of the target [NavigationHost] for which to find the traversal context.
 * @param startContext An optional [TraversalContext] that specifies the starting point for the search.
 * If provided, the search will begin from the host(s) specified by this context's
 * points within the current navigation state. If `null`, the search behaves the same
 * as [getTraversalContextForHost] without the start context.
 * @return A [TraversalContext] object containing:
 * - `hosts`: The complete list of hosts in the navigation state
 * - `points`: The ordered list of host names representing the path to the target host
 *
 * Returns `null` if a host with the specified name is not found within the navigation
 * state hierarchy or if the start context path is invalid.
 *
 * @throws IllegalArgumentException if the [startContext] contains a path that cannot be found
 * in the navigation state hierarchy.
 *
 * Example usage when searching for a host by name within a specific scope:
 * ```
 * // Scope the search to a specific tab to avoid collisions with same-named hosts elsewhere
 * val startContext = navigationState.inside("Main").inside("Tab1")
 * val context = navigationState.getTraversalContextForHost(
 * targetName = "Settings",
 * startContext = startContext
 * )
 * if (context != null) {
 * val pathToTarget = context.points // e.g., ["Main", "Tab1", "Settings"]
 * val allHosts = context.hosts
 * }
 * ```
 *
 * @see TraversalContext
 * @see NavigationHost
 * @see getTraversalContextForHost
 */
public fun NavigationState.getTraversalContextForHost(
    targetName: String,
    startContext: TraversalContext? = null,
): TraversalContext? {
    if (startContext == null) {
        return getTraversalContextForHost(targetName)
    }

    val startHosts = if (startContext.points.isEmpty()) {
        this.hosts
    } else {
        val path = findShortestPathBFS(this.hosts, startContext.points) ?: return null
        val root = this.hosts.find { it.hostName == path.first() } ?: return null

        if (path.size == ONE) {
            listOf(root)
        } else {
            var currentHost: NavigationHost = root
            for (i in 1 until path.size) {
                currentHost = currentHost.children.find { it.hostName == path[i] } ?: return null
            }
            listOf(currentHost)
        }
    }

    startHosts.forEach { startHost ->
        val path = findHostPathIterative(startHost, targetName)
        if (path != null) {
            return TraversalContext(
                hosts = hosts,
                points = path,
            )
        }
    }

    return null
}

private fun NavigationState.getTraversalContextForHost(targetName: String): TraversalContext? {
    this.hosts.forEach {
        val path = findHostPathIterative(it, targetName)
        if (path != null) {
            return TraversalContext(
                hosts = hosts,
                points = path,
            )
        }
    }
    return null
}

private fun findHostPathIterative(
    current: NavigationHost,
    targetName: String
): List<String>? {
    val stack = mutableListOf<NodeWithPath>()
    stack.add(NodeWithPath(current, listOf(current.hostName)))

    while (stack.isNotEmpty()) {
        val (node, currentPath) = stack.removeAt(stack.size - 1)

        if (node.hostName == targetName) {
            return currentPath
        }

        for (i in node.children.size - 1 downTo 0) {
            val child = node.children[i]
            stack.add(NodeWithPath(child, currentPath + child.hostName))
        }
    }

    return null
}

/**
 * Retrieves the traversal context for a specific navigation host within the navigation state,
 * starting the search from a specific traversal context.
 *
 * This method is useful when there are multiple [NavigationHost] instances with the same name
 * in the navigation state hierarchy. By providing a [startContext], you can specify the starting
 * point for the search, allowing the system to distinguish between different instances of hosts
 * with identical names.
 *
 * The search begins from the host specified by the [startContext]'s points. The points from
 * [startContext] are used to find the starting host within the current navigation state's hosts.
 * If the [startContext] has points, the search starts from the host reached by following that path
 * in the current state. If [startContext] has no points, the search starts from all root hosts
 * in the current navigation state.
 *
 * The search is performed using an iterative depth-first traversal approach, examining each host
 * and its children until the target host is found or all possibilities are exhausted.
 *
 * @param target The target [NavigationHost] for which to find the traversal context.
 * @param startContext An optional [TraversalContext] that specifies the starting point for the search.
 *                    If provided, the search will begin from the host(s) specified by this context's
 *                    points within the current navigation state. If `null`, the search behaves the same
 *                    as [getTraversalContextForHost] without the start context.
 * @return A [TraversalContext] object containing:
 *         - `hosts`: The complete list of hosts in the navigation state
 *         - `points`: The ordered list of host names representing the path to the target host
 *
 *         Returns `null` if the target host is not found within the navigation state hierarchy
 *         or if the start context path is invalid.
 *
 * @throws IllegalArgumentException if the [startContext] contains a path that cannot be found
 *                                  in the navigation state hierarchy.
 *
 * Example usage when there are duplicate host names:
 * ```
 * // Start searching from a specific context
 * val startContext = navigationState.inside("Main").inside("Tab1")
 * val context = navigationState.getTraversalContextForHost(
 *     target = targetHost,
 *     startContext = startContext
 * )
 * if (context != null) {
 *     val pathToTarget = context.points
 *     val allHosts = context.hosts
 * }
 * ```
 *
 * @see TraversalContext
 * @see NavigationHost
 * @see getTraversalContextForHost
 */
public fun NavigationState.getTraversalContextForHost(
    target: NavigationHost,
    startContext: TraversalContext? = null,
): TraversalContext? {
    if (startContext == null) {
        return getTraversalContextForHost(target)
    }

    // Find starting host(s) from the startContext points within the current state
    val startHosts = if (startContext.points.isEmpty()) {
        // If no points, use all root hosts from the current state
        this.hosts
    } else {
        // Find the host by following the path from startContext points in the current state
        val path = findShortestPathBFS(this.hosts, startContext.points)
            ?: return null

        val root = this.hosts.find { it.hostName == path.first() }
            ?: return null

        if (path.size == ONE) {
            listOf(root)
        } else {
            val hostsPath = mutableListOf(root)
            var currentHost: NavigationHost = root

            for (i in 1 until path.size) {
                currentHost = currentHost.children.find { it.hostName == path[i] }
                    ?: return null
                hostsPath.add(currentHost)
            }
            // Start search from the last host in the path (the deepest one)
            listOf(hostsPath.last())
        }
    }

    // Search from each starting host
    startHosts.forEach { startHost ->
        val path = findHostPathIterative(startHost, target)
        if (path != null) {
            return TraversalContext(
                hosts = hosts,
                points = path,
            )
        }
    }

    return null
}

private fun NavigationState.getTraversalContextForHost(target: NavigationHost): TraversalContext? {
    this.hosts.forEach {
        val path = findHostPathIterative(it, target)
        if (path != null) {
            return TraversalContext(
                hosts = hosts,
                points = path,
            )
        }
    }

    return null
}

private fun findHostPathIterative(
    current: NavigationHost,
    target: NavigationHost
): List<String>? {

    val stack = mutableListOf<NodeWithPath>()
    stack.add(NodeWithPath(current, listOf(current.hostName)))

    while (stack.isNotEmpty()) {
        val (node, currentPath) = stack.removeAt(stack.size - 1)

        if (node == target) {
            return currentPath
        }

        for (i in node.children.size - 1 downTo 0) {
            val child = node.children[i]
            stack.add(NodeWithPath(child, currentPath + child.hostName))
        }
    }

    return null
}

private data class NodeWithPath(val host: NavigationHost, val path: List<String>)