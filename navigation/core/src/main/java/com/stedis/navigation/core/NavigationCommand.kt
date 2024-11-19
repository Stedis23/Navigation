package com.stedis.navigation.core

interface NavigationCommand {

    public fun execute(navigationState: NavigationState): NavigationState
}