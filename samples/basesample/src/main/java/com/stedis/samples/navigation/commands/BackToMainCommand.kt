package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.samples.panes.main.MainDestination

object BackToMainCommand : NavigationCommand {
    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            val destination = navigationState.currentHost.stack.find { it is MainDestination }
            val host = (destination as MainDestination).currentSubHost

            BackCommand then ChangeCurrentHostCommand(host)
        }
}