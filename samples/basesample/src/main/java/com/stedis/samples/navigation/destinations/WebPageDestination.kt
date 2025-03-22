package com.stedis.samples.navigation.destinations

import com.stedis.navigation.core.Destination
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebPageDestination(val url: String) : Destination