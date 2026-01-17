package com.stedis.navigation.core

import kotlin.reflect.KClass

private const val NOT_FOUND_INDEX = -1

/**
 * Flattens a list of [NavigationHost] instances into a single [NavigationHost].
 *
 * This method merges the navigation stacks of all hosts in the list into one continuous
 * sequence. The resulting host will have the specified [newHostName] and no children,
 * effectively "collapsing" a multi-host hierarchy into a single linear navigation history.
 *
 * The [NavigationHost.currentDestination] of the new host is determined by the last
 * destination in the combined stack.
 *
 * @param newHostName The name to be assigned to the newly created flattened host.
 * @return A new [NavigationHost] containing the merged stacks of all input hosts.
 * @throws NoSuchElementException if the resulting merged stack is empty (e.g., if the
 * original list is empty or all hosts have empty stacks).
 * * Example usage:
 * ```
 * val combinedHost = listof(hostA, hostB).flatten("MergedHost")
 * ```
 */
public fun List<NavigationHost>.flatten(newHostName: String): NavigationHost {
    val newStack = flatMap { it.stack }

    // Explicitly check to provide a better context if last() would fail
    require(newStack.isNotEmpty()) {
        "Cannot flatten NavigationHosts: The resulting navigation stack is empty."
    }

    return NavigationHost(
        hostName = newHostName,
        currentDestination = newStack.last(),
        stack = newStack,
        children = emptyList(),
        selectedChild = null,
    )
}

/**
 * Finds the first [Destination] of the specified class in the stack.
 *
 * @param destinationClass The class of the destination to search for.
 *
 * @return The first [Destination] instance of the specified class, or
 *         null if no such destination exists in the stack.
 */
public fun NavigationHost.findFirst(destinationClass: KClass<out Destination>): Destination? {
    if (stack.isEmpty()) return null

    val index = stack.indexOfFirst { it::class == destinationClass }
    if (index == NOT_FOUND_INDEX) return null
    return stack[index]
}

/**
 * Finds the first occurrence of the specified [Destination] in the stack.
 *
 * @param destination The [Destination] instance to search for.
 *
 * @return The first matching [Destination] instance, or null if the
 *         specified destination does not exist in the stack.
 */
public fun NavigationHost.findFirst(destination: Destination): Destination? {
    if (stack.isEmpty()) return null

    val index = if (stack.any { it == destination }) {
        stack.indexOf(destination)
    } else return null

    return stack[index]
}

/**
 * Finds the last [Destination] of the specified class in the stack.
 *
 * @param destinationClass The class of the destination to search for.
 *
 * @return The last [Destination] instance of the specified class, or
 *         null if no such destination exists in the stack.
 */
public fun NavigationHost.findLast(destinationClass: KClass<out Destination>): Destination? {
    if (stack.isEmpty()) return null

    val index = stack.indexOfLast { it::class == destinationClass }
    if (index == NOT_FOUND_INDEX) return null
    return stack[index]
}

/**
 * Finds the last occurrence of the specified [Destination] in the stack.
 *
 * @param destination The [Destination] instance to search for.
 *
 * @return The last matching [Destination] instance, or null if the
 *         specified destination does not exist in the stack.
 */
public fun NavigationHost.findLast(destination: Destination): Destination? {
    if (stack.isEmpty()) return null

    val index = if (stack.any { it == destination }) {
        val reverseStack = stack.toMutableList()
        reverseStack.reverse()
        reverseStack.indexOf(destination)
    } else return null

    return stack[index]
}

/**
 * Checks for the presence of consecutive duplicates of a specific destination type in the navigation stack.
 *
 * This function scans through the stack to determine if there are any consecutive elements
 * of the specified destination type. If no type is specified, it uses the type of the last
 * element in the stack as the target for duplicate checking.
 *
 * The function returns `true` if it finds two or more consecutive elements of the same target type,
 * regardless of their object equality. This is useful for detecting navigation patterns where
 * the same screen type appears consecutively in the back stack.
 *
 * @param destinationClass The class of the [Destination] type to check for consecutive duplicates.
 *                         If `null`, the type of the last element in the stack will be used.
 * @return `true` if consecutive duplicates of the target type are found, `false` otherwise.
 *         Also returns `false` if the stack is empty.
 *
 * Example usage:
 * ```
 * val hasDuplicates = navigationHost.hasConsecutiveDuplicates(ProfileDestination::class)
 * if (hasDuplicates) {
 *     // Clean up consecutive duplicates
 * }
 * ```
 *
 * @see removeConsecutiveDuplicates
 */
public fun NavigationHost.hasConsecutiveDuplicates(destinationClass: KClass<out Destination>? = null): Boolean {
    if (stack.isEmpty()) return false

    val targetDestination = destinationClass ?: stack.last()::class
    var previous: Destination? = null

    for (current in stack) {
        if (current::class == targetDestination) {
            if (previous != null && current::class == previous::class) {
                return true
            }
            previous = current
        }
    }

    return false
}