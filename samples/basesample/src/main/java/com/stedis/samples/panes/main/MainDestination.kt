package com.stedis.samples.panes.main

import androidx.compose.runtime.Composable
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.NoSaveState
import com.stedis.navigation.core.Destination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@NoSaveState
@Parcelize
data class MainDestination(val currentSubHost: String) : ComposeDestination {

    @IgnoredOnParcel
    override val composable: @Composable (Destination) -> Unit = {
        MainPane((it as MainDestination).currentSubHost)
    }
}