package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.ForwardCommand
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState

class ForwardCommand(private val destination: Destination, private val host: String? = null) :
    NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            if (host != null) {
                ChangeCurrentHostCommand(host) then
                        ForwardCommand(destination)
            } else {
                ForwardCommand(destination)
            }
        }
}