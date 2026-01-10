package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.scenes.ListDetailScene
import com.stedis.navigation.compose.setAnimationSpec
import com.stedis.samples.ui.panes.friends.friend.FriendInfoPane
import kotlinx.parcelize.Parcelize

@Parcelize
class FriendInfoDestination(val friendId: Int) : ComposeDestination() {

    override val content: @Composable () -> Unit = {
        FriendInfoPane(friendId = this.friendId)
    }

    override val metadata: Map<Any, Any> = ListDetailScene.detailPane() + setAnimationSpec()
}