package com.stedis.navigation.core

/**
 * Annotation to specify that a [Destination] is only accessible on a specific [NavigationHost].
 *
 * When a destination is marked with this annotation, it can only be used within the
 * context of the specified host. Attempting to access a destination marked with this
 * annotation from a host that is not specified will result in an exception being thrown.
 *
 * @property hostName The name of the host that is allowed to access the annotated destination.
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class Host(val hostName: String)