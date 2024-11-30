package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable

@Composable
fun ComposeScreen(destination: ComposeDestination) {
    destination.composable.invoke(destination)
}