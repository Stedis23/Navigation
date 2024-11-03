package com.stedis.navigation.ui.compose

import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.stedis.navigation.core.Destination

open class ComposeNavigationGraph(private val associationManager: ComposeAssociationManager) {

    @Composable
    fun Screen(destination: Destination) {
        (destination as Destinations).setAssociationManager(associationManager)
        destination.associate()
        associationManager.getAssociation(destination).invoke()
    }

    abstract class Destinations : Destination, Parcelable {

        private lateinit var associationManager: ComposeAssociationManager

        internal fun setAssociationManager(associationManager: ComposeAssociationManager) {
            this.associationManager = associationManager
        }

        abstract fun associate()

        fun associate(composable: @Composable (Destination) -> Unit) {
            if (!associationManager.findAssociation(this))
                associationManager.addAssociation(ComposeAssociation(this, composable))
        }
    }
}