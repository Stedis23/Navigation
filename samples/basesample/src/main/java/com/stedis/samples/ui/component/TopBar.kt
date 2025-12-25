package com.stedis.samples.ui.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    text: String = "",
) {
    CenterAlignedTopAppBar(
        title = { Text(text = text) },
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(containerColor = MaterialTheme.colorScheme.background)
    )
}