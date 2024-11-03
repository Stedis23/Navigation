package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable
import com.stedis.navigation.core.Destination

class ComposeAssociation(
    val destination: Destination,
    val composable: @Composable (Destination) -> Unit
) {

    @Composable
    operator fun invoke() =
        composable(destination)
}