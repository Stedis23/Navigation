package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.setAnimationSpec
import com.stedis.samples.ui.panes.news.NewsFeedPane
import kotlinx.parcelize.Parcelize

@Parcelize
object NewsFeedDestination : ComposeDestination() {

    override val content: @Composable () -> Unit = {
        NewsFeedPane()
    }

    override val metadata: Map<Any, Any> = setAnimationSpec()
}