package com.stedis.navigation.core

import kotlin.reflect.KClass

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
)

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
            val index = if (_stack.any { it == destination })
                stack.indexOf(destination)
            else throw Error("stack does not contain destination ${destination::class.simpleName}")

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
        )

    companion object {
        private const val ONE = 1
        private const val NOT_FOUND_INDEX = -1
    }
}