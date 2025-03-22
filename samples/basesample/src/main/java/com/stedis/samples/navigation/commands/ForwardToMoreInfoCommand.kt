package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.ForwardCommand
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.panes.info.MoreInfoDestination

object ForwardToMoreInfoCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            ChangeCurrentHostCommand(Hosts.MAIN.name) then
                    ForwardCommand(MoreInfoDestination)
        }
}