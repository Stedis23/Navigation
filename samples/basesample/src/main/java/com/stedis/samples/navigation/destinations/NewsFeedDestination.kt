package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.samples.ui.panes.news.NewsFeedPane
import kotlinx.parcelize.Parcelize

@Parcelize
object NewsFeedDestination : ComposeDestination() {

    override val content: @Composable () -> Unit = {
        NewsFeedPane()
    }
}