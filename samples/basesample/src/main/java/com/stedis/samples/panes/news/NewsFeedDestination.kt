package com.stedis.samples.panes.news

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
object NewsFeedDestination : ComposeDestination {

    @IgnoredOnParcel
    override val composable: @Composable (Destination) -> Unit = {
        NewsFeedPane()
    }
}