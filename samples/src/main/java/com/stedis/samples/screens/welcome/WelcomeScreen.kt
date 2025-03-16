package com.stedis.samples.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.core.execute
import com.stedis.samples.R
import com.stedis.samples.navigation.commands.ForwardToMainCommand

@Composable
fun WelcomeScreen() {
    val navigationManager = LocalNavigationManager.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = { navigationManager.execute(ForwardToMainCommand) }) {
            Text(text = stringResource(R.string.welcome_start))
        }
    }
}