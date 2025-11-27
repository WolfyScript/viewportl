package com.wolfyscript.viewportl.gui.compose.layout

class Offset(
    val x: Dp,
    val y: Dp,
) {
    companion object {

        val Zero = Offset(Dp.Zero, Dp.Zero)

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