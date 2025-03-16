package com.stedis.samples.screens.news

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class NewsFeedDestination : ComposeDestination {
    
    override val composable: @Composable (Destination) -> Unit = {
        NewsFeedScreen()
    }
}