package com.stedis.samples.panes.main

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.NoSaveState
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@NoSaveState
@Parcelize
object MainDestination : ComposeDestination {

    @IgnoredOnParcel
    override val composable: @Composable (Destination) -> Unit = {
        MainPane()
    }
}