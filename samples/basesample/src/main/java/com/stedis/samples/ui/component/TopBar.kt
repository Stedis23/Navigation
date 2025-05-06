package com.stedis.samples.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.stedis.samples.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    text: String = "",
    onClick: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = text)
        },
        navigationIcon = {
            onClick?.let {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                    modifier = Modifier.clickable { it() }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            .copy(containerColor = MaterialTheme.colorScheme.background)
    )
}