package com.stedis.samples.panes.friends.friend

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.Parcelize

@Parcelize
class FriendInfoDestination(private val friendId: String) : ComposeDestination {

    override val composable: @Composable (Destination) -> Unit = {
        FriendInfoPane(friendId = (it as FriendInfoDestination).friendId)
    }
}