package com.stedis.navigation.core

public interface HostDestination : Destination

public interface SlotDestination : HostDestination {

    public val item: Destination?
}

public interface ListDestination : HostDestination {

    public val items: List<Destination>?
}

public interface PagesDestination : HostDestination {

    public val items: List<Destination>
    public val selected: Int
}

public interface LinkedDestination : Destination {

    public val previousDestination: Destination
    public val previousHostName: String
}