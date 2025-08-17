package com.stedis.navigation.core

import kotlinx.coroutines.CoroutineScope

/**
 * The [ScopeDestination] interface represents a navigation target point
 * that provides a coroutine scope for executing asynchronous tasks.
 *
 * @property destinationScope The coroutine scope bound to the lifecycle
 * of the [ScopeDestination]. This scope is used to launch coroutines
 * that will run in a context corresponding to the navigation state.
 *
 * Note that when the [ScopeDestination] is removed from the
 * [NavigationState], the associated [destinationScope] will be cancelled.
 * This ensures that all launched coroutines are properly completed,
 * preventing memory leaks and unnecessary operations that may occur after
 * the navigation target point is removed.
 */
public interface ScopeDestination : Destination {

    public val destinationScope: CoroutineScope
}

public interface HostDestination : Destination

public interface SlotDestination : HostDestination {

    public val item: Destination?
}

public interface ListDestination : HostDestination {

    public val items: List<Destination>?
}

public interface PagesDestination : HostDestination {

    public val items: List<Destination>
    public val selected: Int
}

public interface LinkedDestination : Destination {

    public val previousDestination: Destination
    public val previousHostName: String
}