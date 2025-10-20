package com.wolfyscript.viewportl.gui.compose

interface Modifier {

    infix fun then(other: Modifier): Modifier

}

interface ModifierNode {

    fun onAttach()

    fun onDetach()

}