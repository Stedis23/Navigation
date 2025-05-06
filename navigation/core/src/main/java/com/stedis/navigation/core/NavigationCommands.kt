package com.stedis.navigation.core

import kotlin.reflect.KClass

class ForwardCommand(
    private val destination: Destination,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        if (context == null) {
            navigationState.buildNewStateWithCurrentHost {
                addDestination(destination)
            }
        } else {
            navigationState.buildNewState {
                TraversalContext(
                    hosts = hosts,
                    points = emptyList(),
                )
                    .context()
                    .perform { addDestination(destination) }
            }
        }
}

class BackCommand(
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        if (context == null) {
            navigationState.buildNewStateWithCurrentHost {
                popDestination()
            }
        } else {
            navigationState.buildNewState {
                TraversalContext(
                    hosts = hosts,
                    points = emptyList(),
                )
                    .context()
                    .perform { popDestination() }
            }
        }
}

class BackToCommand(
    private val destinationClass: KClass<out Destination>,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        if (context == null) {
            navigationState.buildNewStateWithCurrentHost {
                popToDestination(destinationClass)
            }
        } else {
            navigationState.buildNewState {
                TraversalContext(
                    hosts = hosts,
                    points = emptyList(),
                )
                    .context()
                    .perform { popToDestination(destinationClass) }
            }
        }
}

class ReplaceCommand(
    private val destination: Destination,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        if (context == null) {
            navigationState.buildNewStateWithCurrentHost {
                replaceDestination(destination)
            }
        } else {
            navigationState.buildNewState {
                TraversalContext(
                    hosts = hosts,
                    points = emptyList(),
                )
                    .context()
                    .perform { replaceDestination(destination) }
            }
        }
}

class OnNewRootCommand(
    private val destination: Destination,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        if (context == null) {
            navigationState.buildNewStateWithCurrentHost {
                popToDestination(stack.first())
                replaceDestination(destination)
            }
        } else {
            navigationState.buildNewState {
                TraversalContext(
                    hosts = hosts,
                    points = emptyList(),
                )
                    .context()
                    .perform {
                        popToDestination(stack.first())
                        replaceDestination(destination)
                    }
            }
        }
}

class OnRootCommand(
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        if (context == null) {
            navigationState.buildNewStateWithCurrentHost {
                popToDestination(stack.first())
            }
        } else {
            navigationState.buildNewState {
                TraversalContext(
                    hosts = hosts,
                    points = emptyList(),
                )
                    .context()
                    .perform { popToDestination(stack.first()) }
            }
        }
}

class ChangeSelectedChildHostCommand(
    private val hostName: String,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        if (context == null) {
            navigationState.buildNewStateWithCurrentHost {
                setSelectedChild(hostName)
            }
        } else {
            navigationState.buildNewState {
                TraversalContext(
                    hosts = hosts,
                    points = emptyList(),
                )
                    .context()
                    .perform { setSelectedChild(hostName) }
            }
        }
}

class ChangeCurrentHostCommand(private val hostName: String) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            setCurrentHost(hostName)
        }
}