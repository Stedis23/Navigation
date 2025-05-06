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
data class TraversalContext(
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
 * the navigation host tree starting from the root host. It constructs a list of `NavigationHost`
 * objects that represent the path from the root to the specified destination.
 *
 * Example of using getHostsPath:
 * ```
 * val path = TraversalContext.inside("Main")
 *                              .inside("News")
 *                              .getHostsPath()
 * ```
 *
 * @return A list of [NavigationHost] representing the path through the host tree.
 */
public fun TraversalContext.getHostsPath(): List<NavigationHost> {
    val path: List<String> = findShortestPathBFS(hosts, points)
        ?: throw error("The given path was not found in the host tree")

    val root = hosts.find { it.hostName == path.first() } ?: throw error("root host can`t be null")

    val hostsPath = mutableListOf(root)

    if (path.size != ONE) {
        var tail = path.toMutableList().drop(ONE)
        var currentHost: NavigationHost = root
        while (tail.isNotEmpty()) {
            currentHost = currentHost.children.find { it.hostName == tail.first() }
                ?: throw error("host ${tail.first()} not exist")
            hostsPath.add(currentHost)
            tail = tail.toMutableList().drop(ONE)
        }
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

internal fun modifyHost(
    roots: List<NavigationHost>,
    path: List<String>,
    body: NavigationHostBuilder.() -> Unit
): List<NavigationHost> {
    val head = path.first()
    val tail = path.drop(ONE)
    val root = roots.find { it.hostName == head } ?: throw error("root host can`t be null")

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
                root.modifyChild(tail, body)
            }
        } else {
            it
        }
    }
}

private fun NavigationHost.modifyChild(
    path: List<String>,
    body: NavigationHostBuilder.() -> Unit
): NavigationHost {
    val newChildren = children.map {
        if (it.hostName == path.first()) {
            if (path.size == ONE) {
                NavigationHostBuilder(it.hostName, it.currentDestination)
                    .updateStack(it.stack)
                    .updateChildren(it.children)
                    .setSelectedChild(it.selectedChild?.hostName)
                    .apply { body() }
                    .build()
            } else {
                val newPath = path.drop(ONE)
                it.modifyChild(newPath, body)
            }
        } else {
            it
        }
    }

    val newSelectedChild = newChildren.find { it.hostName == selectedChild?.hostName }

    return copy(
        selectedChild = newSelectedChild,
        children = newChildren,
    )
}


internal fun findShortestPathBFS(roots: List<NavigationHost>, points: List<String>): List<String>? {
    val queue: LinkedList<Pair<NavigationHost, List<String>>> = LinkedList()

    for (root in roots) {
        queue.add(Pair(root, listOf(root.hostName)))
    }

    while (queue.isNotEmpty()) {
        val (currentNode, path) = queue.poll()

        if (points.contains(currentNode.hostName) && path.toSet().containsAll(points)) {
            return path
        }

        for (child in currentNode.children) {
            queue.add(Pair(child, path + child.hostName))
        }
    }

    return null
}