package com.stedis.navigation.core

import kotlin.reflect.KClass

public class ForwardCommand(
    private val destination: Destination,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
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

public class BackCommand(
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
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

public class BackToCommand(
    private val destinationClass: KClass<out Destination>,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
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

public class ReplaceCommand(
    private val destination: Destination,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
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

public class OnNewRootCommand(
    private val destination: Destination,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
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

public class OnRootCommand(
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
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

public class ChangeSelectedChildHostCommand(
    private val hostName: String,
    private val context: (TraversalContext.() -> TraversalContext)? = null,
) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
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

public class ChangeCurrentHostCommand(private val hostName: String) : NavigationCommand {

    public override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            setCurrentHost(hostName)
        }
}