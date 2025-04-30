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
                root.findChild(tail, body)
            }
        } else {
            it
        }
    }
}

private fun NavigationHost.findChild(
    path: List<String>,
    body: NavigationHostBuilder.() -> Unit
): NavigationHost =
    copy(
        children = children.map {
            if (it.hostName == path.first()) {
                if (path.size == ONE) {
                    NavigationHostBuilder(it.hostName, it.currentDestination)
                        .updateStack(it.stack)
                        .updateChildren(it.children)
                        .apply { body() }
                        .build()
                } else {
                    val newPath = path.drop(ONE)
                    it.findChild(newPath, body)
                }
            } else {
                it
            }
        }
    )

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