package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.ReplaceCommand
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.SubHostsHistory
import com.stedis.samples.navigation.destinations.put
import com.stedis.samples.panes.main.MainDestination

class ChangeCurrentSubHostOnMainCommand(private val hostName: String) : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            val subHostsHistory =
                currentState.hosts.find { it.hostName == Hosts.MAIN_SUB_HOSTS.name }?.currentDestination
                    ?: error("host: MAIN_HOSTS don`t exist")

            val newSubHostsHistory = (subHostsHistory as SubHostsHistory).put(hostName)

            ChangeCurrentHostCommand(Hosts.MAIN_SUB_HOSTS.name) then
                    ReplaceCommand(newSubHostsHistory) then
                    ChangeCurrentHostCommand(Hosts.GLOBAL.name) then
                    ReplaceCommand(MainDestination(hostName)) then
                    ChangeCurrentHostCommand(hostName)
        }
}