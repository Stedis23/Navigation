package com.stedis.navigation.ui.compose

import com.stedis.navigation.core.Destination
import kotlin.reflect.KClass

class ComposeAssociationManager {
    private val mapping: MutableMap<KClass<*>, ComposeAssociation> = mutableMapOf()

    fun getAssociation(destination: Destination): ComposeAssociation =
        mapping[destination::class]
            ?: throw error("ComposeAssociation cannot be null for destination")

    fun addAssociation(composable: ComposeAssociation) {
        mapping[composable.destination::class] = composable
    }

    fun findAssociation(destination: Destination): Boolean =
        mapping[destination::class] != null
}