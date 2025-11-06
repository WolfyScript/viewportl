package com.wolfyscript.viewportl.gui.compose.layout

class Offset(
    val x: Size,
    val y: Size,
) {
    companion object {

        val Zero = Offset(Size.Zero, Size.Zero)

    }

    operator fun plus(offset: Offset): Offset {
        return Offset(x + offset.x, y + offset.y)
    }

    operator fun minus(offset: Offset): Offset {
        return Offset(x - offset.x, y - offset.y)
    }

    operator fun times(factor: Int): Offset {
        return Offset(x * factor, y * factor)
    }

    override fun toString(): String {
        return "(x=$x, y=$y)"
    }

}