package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable
import com.stedis.navigation.core.Destination

interface ComposeDestination : Destination {

    val composable: @Composable (Destination) -> Unit
}