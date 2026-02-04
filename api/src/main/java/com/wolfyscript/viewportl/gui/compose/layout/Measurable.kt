package com.wolfyscript.viewportl.gui.compose.layout

interface Measurable {

    val scopeData: Any?

    fun measure(constraints: Constraints) : Placeable

}