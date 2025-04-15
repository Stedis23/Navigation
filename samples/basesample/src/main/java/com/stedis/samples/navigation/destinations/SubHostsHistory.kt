package com.stedis.samples.navigation.destinations

import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.NavigationState
import com.stedis.navigation.core.findHost
import com.stedis.samples.navigation.Hosts
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubHostsHistory(val hosts: Set<String>) : Destination

fun SubHostsHistory.open(host: String): SubHostsHistory {
    val newHosts = hosts.filter { it != host }.toMutableSet()
    newHosts.add(host)

    return SubHostsHistory(newHosts.toSet())
}

fun SubHostsHistory.close(): SubHostsHistory {
    val newHosts = hosts.toMutableSet()
    newHosts.remove(hosts.last())

    return SubHostsHistory(newHosts.toSet())
}

fun NavigationState.getSubHostsHistory() =
    findHost(Hosts.MAIN_SUB_HOSTS.name)?.currentDestination
        ?: error("host: ${Hosts.MAIN_SUB_HOSTS.name} don`t exist")