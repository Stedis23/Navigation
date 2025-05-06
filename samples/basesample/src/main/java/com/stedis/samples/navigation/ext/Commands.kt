package com.stedis.samples.navigation.ext

import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.ForwardCommand
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.TraversalContext
import com.stedis.navigation.core.execute
import com.stedis.navigation.core.inside

fun NavigationManager.forward(
    hostName: String,
    context: (TraversalContext.() -> TraversalContext)? = null,
) {
    execute(
        NavigationCommand {
            val context = context?.let { traversalContext.it() } ?: traversalContext
            context
                .inside(hostName)
                .switch()
        }
    )
}

fun NavigationManager.forward(
    destination: Destination,
    context: (TraversalContext.() -> TraversalContext)? = null,
) {
    execute(
        CommandsChain {
            ForwardCommand(destination, context) then
                    NavigationCommand {
                        context?.let {
                            traversalContext
                                .context()
                                .switch()
                        }
                    }
        }
    )
}

fun NavigationManager.back(context: (TraversalContext.() -> TraversalContext)? = null) {
    execute(BackCommand(context))
}