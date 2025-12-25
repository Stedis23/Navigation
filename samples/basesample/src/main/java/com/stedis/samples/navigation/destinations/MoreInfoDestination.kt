package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.samples.ui.panes.info.MoreInfoPane
import kotlinx.parcelize.Parcelize

@Parcelize
object MoreInfoDestination : ComposeDestination() {

    override val content: @Composable () -> Unit = {
        MoreInfoPane()
    }
}