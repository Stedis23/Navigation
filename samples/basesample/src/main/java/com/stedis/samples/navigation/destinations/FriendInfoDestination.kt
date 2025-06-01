package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.core.Destination
import com.stedis.samples.ui.panes.friends.friend.FriendInfoPane
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class FriendInfoDestination(private val friendId: Int) : ComposeDestination {

    @IgnoredOnParcel
    override val composable: @Composable (Destination) -> Unit = {
        FriendInfoPane(friendId = (it as FriendInfoDestination).friendId)
    }
}