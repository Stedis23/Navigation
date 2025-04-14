package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.BackToCommand
import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.ReplaceCommand
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.SubHostsHistory
import com.stedis.samples.navigation.destinations.pop
import com.stedis.samples.panes.main.MainDestination

object BackCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            val stack = currentState.currentHost.stack
            val isLastElement = stack.size == 1

            val isPenultimateMain = if (!isLastElement) {
                stack[stack.size - 2] is MainDestination
            } else {
                false
            }

            when {
                isPenultimateMain -> BackToMainCommand
                !isLastElement -> BackCommand
                else -> BackInsideMainCommand
            }
        }
}

object BackToMainCommand : NavigationCommand {
    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            BackToCommand(MainDestination::class).executeWithUpdateCurrentState() then
                    ChangeCurrentHostCommand((currentState.currentDestination as MainDestination).currentSubHost)
        }
}

object BackInsideMainCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            val subHostsHistory =
                currentState.hosts.find { it.hostName == Hosts.MAIN_SUB_HOSTS.name }?.currentDestination
                    ?: error("host: MAIN_HOSTS don`t exist")

            require((subHostsHistory as SubHostsHistory).hosts.size > 1)

            val newSubHostsHistory = subHostsHistory.pop()
            val subHostName = newSubHostsHistory.hosts.last()

            ChangeCurrentHostCommand(Hosts.MAIN_SUB_HOSTS.name) then
                    ReplaceCommand(newSubHostsHistory) then
                    ChangeCurrentHostCommand(Hosts.GLOBAL.name) then
                    ReplaceCommand(MainDestination(subHostName)) then
                    ChangeCurrentHostCommand(subHostName)
        }
}