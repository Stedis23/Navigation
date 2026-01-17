package com.stedis.navigation.core

import kotlinx.coroutines.channels.Channel

/**
 * A thread-safe queue for managing the execution flow of [NavigationCommand]s.
 *
 * This class acts as a wrapper around a Kotlin [Channel], providing a producer-consumer
 * pattern to handle navigation updates. It ensures that commands are processed in
 * the order they are received (FIFO - First In, First Out), preventing race conditions
 * when multiple navigation events occur simultaneously.
 *
 * @param capacity The maximum number of elements that can be buffered in the queue.
 * Defaults to [Channel.BUFFERED].
 */
internal class CommandsQueue(capacity: Int = Channel.BUFFERED) {

    private val channel = Channel<NavigationCommand>(capacity)

    /**
     * Adds a [NavigationCommand] to the queue.
     * * This is a suspending function. If the queue's buffer is full (backpressure),
     * this method will suspend the caller until space becomes available.
     *
     * @param command The navigation command to be queued.
     */
    suspend fun enqueue(command: NavigationCommand) {
        channel.send(command)
    }

    /**
     * Closes the queue.
     * * Once closed, no further commands can be enqueued. Any attempts to call [enqueue]
     * will result in an exception. Closing the queue signals the consumer that
     * no more commands are expected.
     */
    fun close() {
        channel.close()
    }

    /**
     * Returns the underlying [Channel] for command consumption.
     * * The consumer (usually the [NavigationManager]) can iterate over this channel
     * to process commands sequentially as they arrive.
     *
     * @return A [ReceiveChannel] of navigation commands.
     */
    fun getCommands() = channel
}