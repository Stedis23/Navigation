package com.stedis.navigation.core

class ForwardCommand(private val destination: Destination) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            updateHosts {
                navigationState.hosts.map { host ->
                    if (host.hostName == currentHost.hostName) {
                        host.buildNewHost {
                            addDestination(destination)
                        }
                    } else {
                        host
                    }
                }
            }
        }
}

class BackCommand() : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            updateHosts {
                navigationState.hosts.map { host ->
                    if (host.hostName == currentHost.hostName) {
                        host.buildNewHost {
                            popDestination()
                        }
                    } else {
                        host
                    }
                }
            }
        }
}

class BackToCommand(private val destination: Destination) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            updateHosts {
                navigationState.hosts.map { host ->
                    if (host.hostName == currentHost.hostName) {
                        host.buildNewHost {
                            popToDestination(destination)
                        }
                    } else {
                        host
                    }
                }
            }
        }
}

class ReplaceCommand(private val destination: Destination) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            updateHosts {
                navigationState.hosts.map { host ->
                    if (host.hostName == currentHost.hostName) {
                        host.buildNewHost {
                            replaceDestination(destination)
                        }
                    } else {
                        host
                    }
                }
            }
        }
}

class ChangeCurrentHostCommand(private val hostName: String) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            setCurrentHost(hostName)
        }
}


