package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.setAnimationSpec
import com.stedis.samples.ui.panes.main.MainPane
import kotlinx.parcelize.Parcelize

@Parcelize
object MainDestination : ComposeDestination() {

    override val content: @Composable () -> Unit = {
        MainPane()
    }

    override val metadata: Map<Any, Any> = setAnimationSpec()
}