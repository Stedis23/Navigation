package com.stedis.samples.navigation.destinations

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.setAnimationSpec
import com.stedis.samples.ui.panes.welcome.WelcomePane
import kotlinx.parcelize.Parcelize

@Parcelize
object WelcomeDestination : ComposeDestination() {

    override val content: @Composable () -> Unit = {
        WelcomePane()
    }

    override val metadata: Map<Any, Any> = setAnimationSpec()
}