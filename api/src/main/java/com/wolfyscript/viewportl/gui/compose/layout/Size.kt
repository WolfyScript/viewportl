package com.wolfyscript.viewportl.gui.compose.layout

class Size(
    val slot: Slot? = null,
    val pixel: Pixel? = null,
)

@JvmInline
value class Slot(val value: Int)

@JvmInline
value class Pixel(val value: Int)
