package com.stedis.samples.panes.info

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
object MoreInfoDestination : ComposeDestination {

    @IgnoredOnParcel
    override val composable: @Composable (Destination) -> Unit = {
        MoreInfoPane()
    }
}