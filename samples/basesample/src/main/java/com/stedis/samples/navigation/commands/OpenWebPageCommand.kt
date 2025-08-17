package com.stedis.samples.navigation.commands

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationState

class OpenWebPageCommand(
    private val uri: Uri,
    private val context: Context,
) : NavigationCommand {
    override fun execute(navigationState: NavigationState): NavigationState {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.localizedMessage?.let { Log.e("ActivityNotFoundException", it) }
        }

        return navigationState
    }
}