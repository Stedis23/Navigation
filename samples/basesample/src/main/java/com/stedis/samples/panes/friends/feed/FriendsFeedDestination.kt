package com.stedis.samples.panes.friends.feed

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.Parcelize

@Parcelize
object FriendsFeedDestination : ComposeDestination {

    override val composable: @Composable (Destination) -> Unit = {
        FriendsFeedPane()
    }
}