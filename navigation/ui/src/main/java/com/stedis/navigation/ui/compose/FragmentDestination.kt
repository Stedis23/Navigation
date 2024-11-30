package com.stedis.navigation.ui.compose

import androidx.fragment.app.Fragment
import com.stedis.navigation.core.Destination

interface FragmentDestination : Destination {

    public fun createFragment(): Fragment
}