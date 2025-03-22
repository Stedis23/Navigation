package com.stedis.samples.panes.info

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.Parcelize

@Parcelize
object MoreInfoDestination : ComposeDestination {

    override val composable: @Composable (Destination) -> Unit = {
        MoreInfoPane()
    }
}