package com.stedis.samples.navigation.ext

import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.execute
import com.stedis.samples.navigation.commands.BackCommand
import com.stedis.samples.navigation.commands.ChangeCurrentSubHostOnMainCommand
import com.stedis.samples.navigation.commands.ForwardCommand

fun NavigationManager.open(destination: Destination, host: String? = null) {
    execute(ForwardCommand(destination, host))
}

fun NavigationManager.close() {
    execute(BackCommand)
}

fun NavigationManager.changeCurrentSubHost(host: String) {
    execute(ChangeCurrentSubHostOnMainCommand(host))
}