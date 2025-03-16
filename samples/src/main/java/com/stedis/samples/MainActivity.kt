package com.stedis.samples

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.ComposeScreen
import com.stedis.navigation.compose.Navigation
import com.stedis.navigation.compose.rememberCurrentDestination
import com.stedis.navigation.compose.rememberNavigationManager
import com.stedis.navigation.compose.rememberNavigationState
import com.stedis.navigation.core.NavigationHost
import com.stedis.navigation.core.NavigationState
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.screens.welcome.WelcomeDestination
import com.stedis.samples.ui.theme.SamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SamplesTheme {
                Navigation(
                    rememberNavigationManager(
                        NavigationState(
                            NavigationHost(
                                hostName = Hosts.MAIN.name,
                                initialDestination = WelcomeDestination,
                            )
                        )
                    )
                ) {
                    Log.d("AAAAAAA", rememberNavigationState().hosts.toString())
                    ComposeScreen(rememberCurrentDestination(Hosts.MAIN.name) as ComposeDestination)
                }
            }
        }
    }
}