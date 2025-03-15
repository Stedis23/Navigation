package com.stedis.navigation.compose

/**
 * Annotation that indicates that the state of a [Destination] should not be saved
 * when navigating away from it. By marking a destination with this annotation,
 * the system will always present the screen in its initial state whenever
 * it is navigated back to, effectively ignoring any previously saved state.
 *
 * This is useful for screens where a fresh start is desired each time the user
 * returns, ensuring that no residual data or user input is retained.
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class NoSaveState