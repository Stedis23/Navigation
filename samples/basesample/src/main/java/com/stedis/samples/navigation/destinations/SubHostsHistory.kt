package com.stedis.samples.navigation.destinations

import com.stedis.navigation.core.Destination
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubHostsHistory(val hosts: Set<String>) : Destination

fun SubHostsHistory.put(host: String): SubHostsHistory {
    val newHosts = hosts.filter { it != host }.toMutableSet()
    newHosts.add(host)

    return SubHostsHistory(newHosts.toSet())
}

fun SubHostsHistory.pop(): SubHostsHistory {
    val newHosts = hosts.toMutableSet()
    newHosts.remove(hosts.last())

    return SubHostsHistory(newHosts.toSet())
}