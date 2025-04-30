package com.stedis.navigation.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass

private const val ONE = 1
private const val NOT_FOUND_INDEX = -1

/**
 * The [NavigationHost] class represents a navigation host in the application.
 * It has a unique name and manages a stack of destinations, as well as the current destination at the top of the stack.
 *
 * ### Important Notes
 *
 * * The [NavigationHost] class is not stable for inheritance in 3rd party libraries, as new methods might be added to this class in the future.
 * * The [NavigationHost] class provides a builder pattern for creating instances, which is recommended for use.
 *
 * ### Configuring the NavigationHost
 *
 * The [NavigationHostBuilder] class provides methods for configuring the host and its destinations. See the [NavigationHostBuilder] documentation for details.
 */
@Parcelize
data class NavigationHost(
    /**
     * The unique name of the navigation host.
     */
    val hostName: String,
    /**
     * The current destination at the top of the stack.
     */
    val currentDestination: Destination,
    /**
     * The stack of destinations.
     */
    val stack: List<Destination>,
    /**
     * The list of child navigation hosts.
     */
    val children: List<NavigationHost>,
    /**
     * The current child navigation host.
     */
    val selectedChild: NavigationHost?,
) : Parcelable

/**
 * Creates a new [NavigationHost] instance with the given host name and initial destination.
 *
 * Example of using `NavigationHost`:
 * ```
 * val mainHost = NavigationHost(hostName = "main", initialDestination = RootDestination) {
 *     addDestination(FirstSampleDestination())
 *     addDestination(SecondSampleDestination())
 * }
 *```
 *
 * @param hostName The unique name of the navigation host.
 * @param initialDestination The initial destination to add to the host.
 * @param params Optional configuration block for the host.
 *
 * @return A new [NavigationHost] instance.
 */
public fun NavigationHost(
    hostName: String,
    initialDestination: Destination,
    params: (NavigationHostBuilder.() -> Unit)? = null,
): NavigationHost =
    NavigationHostBuilder(hostName, initialDestination).also { if (params != null) it.params() }
        .build()

/**
 * Finds the first [Destination] of the specified class in the stack.
 *
 * @param destinationClass The class of the destination to search for.
 *
 * @return The first [Destination] instance of the specified class, or
 *         null if no such destination exists in the stack.
 */
public fun NavigationHost.findFirst(destinationClass: KClass<out Destination>): Destination? {
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
    val index = if (stack.any { it == destination }) {
        val reverseStack = stack.toMutableList()
        reverseStack.reverse()
        reverseStack.indexOf(destination)
    } else return null

    return stack[index]
}

/**
 * Creates a new [NavigationHost] instance by copying the given host and applying the optional configuration block.
 *
 * @param host The host to copy.
 * @param params Optional configuration block for the new host.
 *
 * @return A new [NavigationHost] instance.
 */
public fun NavigationHost.buildNewHost(params: (NavigationHostBuilder.() -> Unit)? = null): NavigationHost =
    NavigationHostBuilder(hostName, stack.first()).also {
        it.updateStack(stack)
        if (params != null) it.params()
    }.build()

/**
 * The [NavigationHostBuilder] class provides a builder pattern for creating and configuring [NavigationHost] instances.
 */
public class NavigationHostBuilder(private val hostName: String, initialDestination: Destination) {

    private var _stack = mutableListOf<Destination>(initialDestination)

    /**
     * The current stack of destinations.
     */
    public val stack: List<Destination>
        get() = _stack

    private val currentDestination: Destination
        get() = _stack.last()

    private val children = mutableListOf<NavigationHost>()

    private var selectedChild: NavigationHost? = null

    /**
     * Adds a new child navigation host to the navigation host.
     *
     * @param hostName The name of the child host to add.
     * @param initialDestination The initial destination for the new child host.
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
            children.forEach {
                if (it.hostName == hostName) throw error("Multiple hosts have name: $hostName, hostName must be unique.")
            }

            children += NavigationHost(hostName, initialDestination, body)

            if (children.isEmpty()) {
                setSelectedChild(hostName)
            }
        }

    /**
     * Sets the selected child element based on the specified host name.
     *
     * This function searches for a child element with the specified host name (`hostName`).
     * If the child element is found, it is set as the selected one.
     * If the child element is not found, an error is thrown with a message
     * indicating that the specified host does not contain the child host.
     *
     * @param hostName The name of the child host to be set as selected.
     *
     * @throws IllegalArgumentException If no child element with the specified name is found.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun setSelectedChild(hostName: String?) =
        apply {
            if (hostName != null) {
                selectedChild = children.find { it.hostName == hostName }
                    ?: throw error("navigation host does not contain child host: $hostName")
            } else {
                selectedChild = null
            }

        }

    /**
     * Updates the children navigation hosts.
     *
     * @param children The new list of children navigation hosts.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun updateChildren(children: List<NavigationHost>) =
        apply {
            this.children.clear()
            this.children.addAll(children)
        }

    /**
     * Updates the stack of destinations.
     *
     * @param stack The new stack of destinations.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun updateStack(stack: List<Destination>) =
        apply {
            _stack.clear()
            _stack.addAll(stack)
        }

    /**
     * Adds a new destination to the stack.
     *
     * @param destination The destination to add.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun addDestination(destination: Destination) =
        apply {
            checkDestination(destination)

            _stack.add(destination)
        }

    /**
     * Replaces the current destination with a new one.
     *
     * @param destination The new destination to replace the current one.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun replaceDestination(destination: Destination) =
        apply {
            checkDestination(destination)

            _stack = _stack.dropLast(ONE).toMutableList()
            _stack.add(destination)
        }

    /**
     * Pops the current destination from the stack.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun popDestination() =
        apply {
            checkStackSize()
            _stack = _stack.dropLast(ONE).toMutableList()
        }

    /**
     * Pops destinations from the stack until the given destination is reached.
     *
     * @param destinationClass The class of the destination to pop to.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun popToDestination(destinationClass: KClass<out Destination>) =
        apply {
            checkDestination(destinationClass)
            checkStackSize()
            val index = _stack.indexOfLast { it::class == destinationClass }
            if (index == NOT_FOUND_INDEX) throw Error("stack does not contain destination ${destinationClass.simpleName}")

            _stack = _stack.dropLast(_stack.size - index - ONE).toMutableList()
        }

    /**
     * Pops destinations from the stack until the given destination is reached.
     *
     * @param destination The destination to pop to.
     *
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun popToDestination(destination: Destination) =
        apply {
            checkDestination(destination)
            checkStackSize()
            val index = if (_stack.any { it == destination }) {
                stack.indexOf(destination)
            } else throw Error("stack does not contain destination ${destination::class.simpleName}")

            _stack = _stack.dropLast(_stack.size - index - ONE).toMutableList()
        }

    private fun checkStackSize() {
        if (_stack.size == ONE) throw error("stack size cannot be less than 1")
    }

    private fun checkDestination(destination: Destination) {
        val hostNames = destination::class.annotations.filterIsInstance<Host>()
        if (hostNames.isNotEmpty() && hostNames.none { it.hostName == hostName })
            throw error("destination: ${destination::class.simpleName} can't be add in host: $hostName")
    }

    private fun checkDestination(destinationClass: KClass<out Destination>) {
        val hostNames = destinationClass.annotations.filterIsInstance<Host>()
        if (hostNames.isNotEmpty() && hostNames.none { it.hostName == hostName })
            throw error("destination: ${destinationClass.simpleName} can't be add in host: $hostName")
    }

    /**
     * Builds a new [NavigationHost] instance.
     *
     * @return A new [NavigationHost] instance.
     */
    public fun build(): NavigationHost =
        NavigationHost(
            hostName = hostName,
            currentDestination = currentDestination,
            stack = stack,
            children = children,
            selectedChild = selectedChild,
        )
}