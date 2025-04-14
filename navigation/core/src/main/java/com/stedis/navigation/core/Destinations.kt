package com.stedis.navigation.core

interface HostDestination : Destination

interface SlotDestination : HostDestination {

    public val item: Destination?
}

interface ListDestination : HostDestination {

    public val items: List<Destination>?
}

interface PagesDestination : HostDestination {

    public val items: List<Destination>
    public val selected: Int
}

interface LinkedDestination : Destination {

    public val previousDestination: Destination
    public val previousHostName: String
}