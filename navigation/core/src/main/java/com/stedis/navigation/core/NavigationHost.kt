package com.stedis.navigation.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass

private const val ONE = 1
private const val NOT_FOUND_INDEX = -1

public typealias HostBuilderDeclaration = NavigationHostBuilder.() -> Unit

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
public data class NavigationHost(
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
@NavigationDslMarker
public fun NavigationHost(
    hostName: String,
    initialDestination: Destination,
    params: HostBuilderDeclaration? = null,
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
@NavigationDslMarker
public fun NavigationHost.buildNewHost(params: HostBuilderDeclaration? = null): NavigationHost =
    NavigationHostBuilder(hostName, stack.first())
        .updateStack(stack)
        .updateChildren(children)
        .setSelectedChild(selectedChild?.hostName)
        .also {
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
    @NavigationDslMarker
    public fun Host(
        hostName: String,
        initialDestination: Destination,
        body: HostBuilderDeclaration? = null
    ) =
        apply {
            children.forEach {
                require(it.hostName != hostName) {
                    "Multiple hosts have name: $hostName, hostName must be unique."
                }
            }

            children += NavigationHost(hostName, initialDestination, body)

            if (children.isEmpty()) {
                setSelectedChild(hostName)
            }
        }

    /**
     * Adds a new child navigation host to the navigation host.
     *
     * @param navigationHost The child host to add.
     *
     * @return The current instance of [NavigationStateBuilder].
     */
    public fun addHost(navigationHost: NavigationHost) =
        apply {
            children.forEach {
                require(it.hostName != navigationHost.hostName) {
                    "Multiple hosts have name: $hostName, hostName must be unique."
                }
            }

            children += navigationHost

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
                    ?: throw IllegalArgumentException("navigation host does not contain child host: $hostName")
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
            require(index != NOT_FOUND_INDEX) {
                "stack does not contain destination ${destinationClass.simpleName}"
            }

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
        require(_stack.size != ONE) { "stack size cannot be less than 1" }
    }

    private fun checkDestination(destination: Destination) {
        val hostNames = destination::class.annotations.filterIsInstance<Host>()
        require(hostNames.isEmpty() || hostNames.any { it.hostName == hostName }) {
            "destination: ${destination::class.simpleName} can't be add in host: $hostName"
        }
    }

    private fun checkDestination(destinationClass: KClass<out Destination>) {
        val hostNames = destinationClass.annotations.filterIsInstance<Host>()

        require(hostNames.isEmpty() || hostNames.any { it.hostName == hostName }) {
            "destination: ${destinationClass.simpleName} can't be add in host: $hostName"
        }
    }

    /**
     * Removes consecutive duplicates of a specific type from the stack.
     *
     * This function iterates through the stack in reverse order and constructs a new stack
     * that contains only unique instances of the specified type. If no type is specified,
     * the function will consider the type of the last element in the stack.
     *
     * The removal of duplicates is based on the equality of the objects themselves.
     * If two consecutive elements are of the same type and are equal, the second occurrence
     * will be removed from the new stack. The resulting stack will replace the original stack.
     *
     * @param destinationClass The class of the destination type to check for duplicates.
     *                         If null, the type of the last element in the stack will be used.
     * @return A new [NavigationHostBuilder] instance.
     */
    public fun removeConsecutiveDuplicates(destinationClass: KClass<out Destination>? = null) =
        apply {
            val newStack = mutableListOf<Destination>()
            val targetDestination = destinationClass ?: _stack.last()::class
            var targetDestinationIndex = newStack.indexOfLast { it::class == targetDestination }

            if (targetDestinationIndex == NOT_FOUND_INDEX) {
                targetDestinationIndex = _stack.size - 1
            }

            var previous: Destination? = null

            for (i in targetDestinationIndex downTo 0) {
                val current = _stack[i]
                if (previous == null || current::class != previous::class) {
                    newStack.add(0, current)
                    previous = current
                }
            }

            _stack = newStack
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