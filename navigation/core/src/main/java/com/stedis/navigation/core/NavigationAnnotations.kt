package com.stedis.navigation.core

@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class Host(val hostName: String)