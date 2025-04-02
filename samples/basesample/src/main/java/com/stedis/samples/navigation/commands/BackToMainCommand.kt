package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.BackToCommand
import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.samples.panes.main.MainDestination

object BackToMainCommand : NavigationCommand {
    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            BackToCommand(MainDestination::class).executeWithUpdateCurrentState() then
                    ChangeCurrentHostCommand((currentState.currentDestination as MainDestination).currentSubHost)
        }
}