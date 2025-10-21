package com.wolfyscript.viewportl.gui.compose.layout

import com.wolfyscript.viewportl.gui.compose.Node

interface Arranger : Measurable, Placeable {

    val node: Node


}