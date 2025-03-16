package com.stedis.samples.screens.messenger

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.NoSaveState
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class MessengerFeedDestination : ComposeDestination {

    override val composable: @Composable (Destination) -> Unit = {
        MessengerFeedScreen()
    }
}