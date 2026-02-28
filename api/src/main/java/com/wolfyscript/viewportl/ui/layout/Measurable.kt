package com.wolfyscript.viewportl.ui.layout

interface Measurable : IntrinsicMeasurable {

    fun measure(constraints: Constraints) : Placeable

}