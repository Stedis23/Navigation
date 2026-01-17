package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.buildNewState
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.destinations.FriendsFeedDestination
import com.stedis.samples.navigation.destinations.MainDestination
import com.stedis.samples.navigation.destinations.NewsFeedDestination

object ForwardToMainCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        navigationState.buildNewState {
            Host(
                hostName = Hosts.MAIN.name,
                initialDestination = MainDestination
            ) {
                Host(
                    hostName = Hosts.FRIENDS.name,
                    initialDestination = FriendsFeedDestination(),
                )

                Host(
                    hostName = Hosts.NEWS.name,
                    initialDestination = NewsFeedDestination,
                )

                setSelectedChild(Hosts.FRIENDS.name)
            }

            setCurrentHost(Hosts.MAIN.name)
            removeHost(Hosts.GLOBAL.name)
        }
}