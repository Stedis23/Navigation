package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.ReplaceCommand
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.SubHostsHistory
import com.stedis.samples.navigation.destinations.getSubHostsHistory
import com.stedis.samples.navigation.destinations.open
import com.stedis.samples.panes.main.MainDestination

class ChangeCurrentSubHostOnMainCommand(private val hostName: String) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            val subHostsHistory = currentState.getSubHostsHistory()
            val newSubHostsHistory = (subHostsHistory as SubHostsHistory).open(hostName)

            ChangeCurrentHostCommand(Hosts.MAIN_SUB_HOSTS.name) then
                    ReplaceCommand(newSubHostsHistory) then
                    ChangeCurrentHostCommand(Hosts.GLOBAL.name) then
                    ReplaceCommand(MainDestination(hostName)) then
                    ChangeCurrentHostCommand(hostName)
        }
}