package com.stedis.samples.navigation.ext

import com.stedis.navigation.core.BackCommand
import com.stedis.navigation.core.CommandsChain
import com.stedis.navigation.core.Destination
import com.stedis.navigation.core.ForwardCommand
import com.stedis.navigation.core.NavigationCommand
import com.stedis.navigation.core.NavigationManager
import com.stedis.navigation.core.TraversalContext
import com.stedis.navigation.core.execute
import com.stedis.navigation.core.getHostsPath
import com.stedis.navigation.core.hasConsecutiveDuplicates
import com.stedis.navigation.core.inside

fun NavigationManager.forward(
    hostName: String,
    context: (TraversalContext.() -> TraversalContext)? = null,
) {
    execute(
        NavigationCommand {
            val context = context?.let { it(traversalContext) } ?: traversalContext
            context.switch(hostName)
        }
    )
}

// This extension is an example of a command that is designed to solve all possible scenarios in an application.
// You can create a similar command or create an atomic command for each individual case.
fun NavigationManager.forward(
    destination: Destination,
    context: (TraversalContext.() -> TraversalContext)? = null,
) {
    execute(
        CommandsChain {
            // A new destination is added to the selected stack.
            ForwardCommand(destination, context) then
                    // In the tree, the path to the host containing the modified stack is switched.
                    NavigationCommand {
                        val host = context?.let { it(traversalContext).getHostsPath().last() }
                            ?: currentHost
                        context?.invoke(traversalContext)?.switch(host.hostName)
                    } then
                    // Consecutive duplicates of the added destination path are removed from the selected stack.
                    // This is necessary to prevent multiple destination paths of the same type from being added to the stack in a row.
                    // This approach helps ensure correct navigation results in the friends list feature in a scene using multiple panels.
                    NavigationCommand {
                        val host = context?.let { it(traversalContext).getHostsPath().last() }
                            ?: currentHost

                        if (host.hasConsecutiveDuplicates()) {
                            val traversalContext = context?.let { it(traversalContext) }
                                ?: traversalContext.inside(host.hostName)

                            traversalContext.perform { removeConsecutiveDuplicates() }
                        }
                    }
        }
    )
}

fun NavigationManager.back(context: (TraversalContext.() -> TraversalContext)? = null) {
    execute(BackCommand(context))
}