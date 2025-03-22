package com.stedis.samples.navigation.commands

import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.ReplaceCommand
import com.stedis.navigation.core.buildNewState
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.panes.friends.feed.FriendsFeedDestination
import com.stedis.samples.panes.main.MainDestination
import com.stedis.samples.panes.news.NewsFeedDestination

object ForwardToMainCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            ReplaceCommand(MainDestination(Hosts.FRIENDS.name))
        }.buildNewState {
            Host(
                hostName = Hosts.FRIENDS.name,
                initialDestination = FriendsFeedDestination,
            )

            Host(
                hostName = Hosts.NEWS.name,
                initialDestination = NewsFeedDestination,
            )

            setCurrentHost(Hosts.FRIENDS.name)
        }
}