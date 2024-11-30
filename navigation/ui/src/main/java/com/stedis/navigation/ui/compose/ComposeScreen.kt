package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable

@Composable
public fun ComposeScreen(destination: ComposeDestination) {
    destination.composable.invoke(destination)
}