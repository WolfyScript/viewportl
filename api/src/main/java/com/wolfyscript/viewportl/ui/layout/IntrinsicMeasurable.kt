package com.wolfyscript.viewportl.ui.layout

interface IntrinsicMeasurable {

    val scopeData: Any?

    fun minIntrinsicWidth(height: Dp): Dp

    fun maxIntrinsicWidth(height: Dp): Dp

    fun minIntrinsicHeight(width: Dp): Dp

    fun maxIntrinsicHeight(width: Dp): Dp

}