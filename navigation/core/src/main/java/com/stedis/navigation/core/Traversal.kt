package com.stedis.navigation.core

import java.util.LinkedList

private const val ONE = 1

/**
 * Represents the context of a traversal operation, containing a list of navigation hosts
 * and a list of points that indicate the path taken during the traversal.
 *
 * @property hosts A list of navigation hosts involved in the traversal.
 * @property points A list of strings representing the points visited during the traversal.
 */
public data class TraversalContext(
    val hosts: List<NavigationHost>,
    val points: List<String>,
)

/**
 * Returns an empty TraversalContext.
 */
public fun emptyTraversalContext(): TraversalContext =
    TraversalContext(hosts = emptyList(), points = emptyList())

/**
 * Retrieves the path of navigation hosts from the current context.
 *
 * This method uses a breadth-first search (BFS) algorithm to find the shortest path through
 * the navigation host tree starting from the root host. Once the path is found, it constructs
 * a list of [NavigationHost] objects iteratively by traversing the tree along the path,
 * ensuring efficient memory usage without recursive calls.
 *
 * Example of using getHostsPath:
 * ```
 * val path = TraversalContext.inside("Main")
 *                              .inside("News")
 *                              .getHostsPath()
 * ```
 *
 * @return A list of [NavigationHost] representing the path through the host tree from root to target.
 *
 * @throws IllegalArgumentException If the path cannot be found in the host tree or if the root host is null.
 */
public fun TraversalContext.getHostsPath(): List<NavigationHost> {
    val path: List<String> = findShortestPathBFS(hosts, points)
        ?: throw IllegalArgumentException("The given path was not found in the host tree")

    val root = hosts.find { it.hostName == path.first() }
        ?: throw IllegalArgumentException("root host can`t be null")

    if (path.size == ONE) {
        return listOf(root)
    }

    val hostsPath = mutableListOf(root)
    var currentHost: NavigationHost = root

    for (i in 1 until path.size) {
        currentHost = currentHost.children.find { it.hostName == path[i] }
            ?: throw IllegalArgumentException("host ${path[i]} not exist")
        hostsPath.add(currentHost)
    }

    return hostsPath
}

/**
 * Creates a new [TraversalContext] with the current [NavigationState] and a specified host name.
 *
 * This infix function adds the specified host name as the first point in the traversal context.
 *
 * @param hostName The name of the host to be added to the traversal context.
 *
 * @return A new [TraversalContext] instance containing the current hosts and the specified host name as the first point.
 */
public infix fun NavigationState.inside(hostName: String): TraversalContext =
    TraversalContext(
        hosts = hosts,
        points = listOf(hostName),
    )

/**
 * Creates a new [TraversalContext] by adding a specified host name to the existing points.
 *
 * This infix function appends the specified host name to the list of points in the current traversal context.
 *
 * @param hostName The name of the host to be added to the points of the traversal context.
 *
 * @return A new [TraversalContext] instance with the updated points.
 */
public infix fun TraversalContext.inside(hostName: String): TraversalContext =
    this.copy(points = points + hostName)

/**
 * Modifies a navigation host at the specified path using an iterative algorithm.
 *
 * This function finds the target host in the navigation tree by following the provided path
 * and applies the configuration block to modify it. The modification is performed iteratively
 * to avoid stack overflow on deep paths.
 *
 * @param roots The list of root navigation hosts to search in.
 * @param path The path to the target host, represented as a list of host names.
 * @param body A lambda function that configures the [NavigationHostBuilder] for the target host.
 *
 * @return A new list of [NavigationHost] instances with the modified host.
 *
 * @throws IllegalArgumentException If the root host cannot be found or if the path is invalid.
 */
internal fun modifyHost(
    roots: List<NavigationHost>,
    path: List<String>,
    body: HostBuilderDeclaration,
): List<NavigationHost> {
    val head = path.first()
    val root = roots.find { it.hostName == head }
        ?: throw IllegalArgumentException("root host can`t be null")

    return roots.map {
        if (it.hostName == head) {
            if (path.size == ONE) {
                NavigationHostBuilder(it.hostName, initialDestination = it.currentDestination)
                    .updateStack(it.stack)
                    .updateChildren(it.children)
                    .setSelectedChild(it.selectedChild?.hostName)
                    .also { it.body() }
                    .build()
            } else {
                root.modifyChildIterative(path, body)
            }
        } else {
            it
        }
    }
}

/**
 * Modifies a child host in the navigation tree using an iterative approach.
 *
 * This extension function builds the chain of hosts from the current host to the target,
 * modifies the target host using the provided configuration block, and then reconstructs
 * the tree from bottom to top, preserving immutability of the navigation host structure.
 *
 * The algorithm works in two phases:
 * 1. Build phase: Traverses the path and collects all hosts in the chain.
 * 2. Rebuild phase: Reconstructs the tree from the modified target upward, creating
 *    new instances of parent hosts with updated children.
 *
 * @param path The path to the target host (path[0] is the current host, path[1..] is the path to the target).
 * @param body A lambda function that configures the [NavigationHostBuilder] for the target host.
 *
 * @return A new [NavigationHost] instance with the modified child host.
 *
 * @throws IllegalArgumentException If the path is invalid or if any host in the path cannot be found.
 */
private fun NavigationHost.modifyChildIterative(
    path: List<String>,
    body: HostBuilderDeclaration,
): NavigationHost {
    if (path.size <= 1) {
        throw IllegalArgumentException("Path must contain at least root and one child")
    }

    val hostChain = mutableListOf<NavigationHost>()
    var currentHost: NavigationHost = this
    hostChain.add(currentHost)

    for (i in 1 until path.size) {
        val nextHostName = path[i]
        currentHost = currentHost.children.find { it.hostName == nextHostName }
            ?: throw IllegalArgumentException("host $nextHostName not exist")
        hostChain.add(currentHost)
    }

    val targetHost = hostChain.last()
    val modifiedTargetHost =
        NavigationHostBuilder(targetHost.hostName, targetHost.currentDestination)
            .updateStack(targetHost.stack)
            .updateChildren(targetHost.children)
            .setSelectedChild(targetHost.selectedChild?.hostName)
            .apply { body() }
            .build()

    var result = modifiedTargetHost

    for (i in hostChain.size - 2 downTo 0) {
        val parentHost = hostChain[i]
        val childHostName = path[i + 1]

        val updatedChildren = parentHost.children.map { child ->
            if (child.hostName == childHostName) {
                result
            } else {
                child
            }
        }
        val updatedSelectedChild =
            updatedChildren.find { it.hostName == parentHost.selectedChild?.hostName }

        result = parentHost.copy(
            selectedChild = updatedSelectedChild,
            children = updatedChildren,
        )
    }

    return result
}


/**
 * Finds the shortest path through the navigation host tree that contains all specified points using BFS.
 *
 * This function uses an optimized breadth-first search algorithm that maintains both the path
 * and a set of visited host names simultaneously to efficiently check if all required points
 * are included in the current path.
 *
 * The algorithm:
 * - Uses a queue to perform BFS traversal starting from root hosts.
 * - Maintains a set of host names along with the path for O(1) membership testing.
 * - Returns the first path that contains all required points, ensuring the shortest path.
 *
 * @param roots The list of root navigation hosts to start the search from.
 * @param points The list of host names that must be included in the path.
 *
 * @return The shortest path as a list of host names, or `null` if no path containing all points exists.
 *         Returns an empty list if the points list is empty.
 */
internal fun findShortestPathBFS(roots: List<NavigationHost>, points: List<String>): List<String>? {
    if (points.isEmpty()) {
        return emptyList()
    }

    val pointsSet = points.toSet()

    data class PathWithSet(val path: List<String>, val pathSet: Set<String>)

    val queue: LinkedList<Pair<NavigationHost, PathWithSet>> = LinkedList()

    roots.forEach {
        val hostName = it.hostName
        queue.add(Pair(it, PathWithSet(listOf(hostName), setOf(hostName))))
    }

    while (queue.isNotEmpty()) {
        val (currentNode, pathWithSet) = queue.poll()

        if (pathWithSet.pathSet.containsAll(pointsSet)) {
            return pathWithSet.path
        }

        currentNode.children.forEach { child ->
            val childName = child.hostName
            val newPathSet = if (pathWithSet.pathSet.contains(childName)) {
                pathWithSet.pathSet
            } else {
                pathWithSet.pathSet + childName
            }
            queue.add(Pair(child, PathWithSet(pathWithSet.path + childName, newPathSet)))
        }
    }

    return null
}