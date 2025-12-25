package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.scenes.ListDetailScene
import com.stedis.samples.ui.panes.friends.feed.FriendsFeedPane
import kotlinx.parcelize.Parcelize

@Parcelize
object FriendsFeedDestination : ComposeDestination() {

    override val content: @Composable () -> Unit = {
        FriendsFeedPane()
    }

    override val metadata: Map<Any, Any> = ListDetailScene.listPane()
}