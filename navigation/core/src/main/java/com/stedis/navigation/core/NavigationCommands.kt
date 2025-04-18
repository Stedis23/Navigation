package com.stedis.navigation.core

import kotlin.reflect.KClass

class ForwardCommand(private val destination: Destination) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewStateWithCurrentHost {
            addDestination(destination)
        }
}

object BackCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewStateWithCurrentHost {
            popDestination()
        }
}

class BackToCommand(private val destinationClass: KClass<out Destination>) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewStateWithCurrentHost {
            popToDestination(destinationClass)
        }
}

class ReplaceCommand(private val destination: Destination) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewStateWithCurrentHost {
            replaceDestination(destination)
        }
}

class OnNewRootCommand(private val destination: Destination) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewStateWithCurrentHost {
            popToDestination(stack.first())
            replaceDestination(destination)
        }
}

object OnRootCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewStateWithCurrentHost {
            popToDestination(stack.first())
        }
}

class ChangeCurrentHostCommand(private val hostName: String) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            setCurrentHost(hostName)
        }
}