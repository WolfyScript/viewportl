package com.wolfyscript.viewportl.gui.compose.layout

class Measurements(
    val width: Size = Size.Zero,
    val height: Size = Size.Zero,
    val placeChildren: PlacementScope.() -> Unit = {},
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Measurements) return false

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + height.hashCode()
        return result
    }

    override fun toString(): String {
        return "(id=${super.toString()}, width=$width, height=$height)"
    }

}