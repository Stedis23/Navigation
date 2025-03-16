package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.ForwardCommand
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.buildNewState
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.screens.main.MainDestination
import com.stedis.samples.screens.messenger.MessengerFeedDestination
import com.stedis.samples.screens.news.NewsFeedDestination

object ForwardToMainCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            ForwardCommand(MainDestination)
        }.buildNewState {
            Host(
                hostName = Hosts.MESSENGER.name,
                initialDestination = MessengerFeedDestination(),
            )

            Host(
                hostName = Hosts.NEWS.name,
                initialDestination = NewsFeedDestination(),
            )

            setCurrentHost(Hosts.NEWS.name)
        }
}