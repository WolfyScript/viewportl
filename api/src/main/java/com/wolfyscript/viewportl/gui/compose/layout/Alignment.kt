package com.wolfyscript.viewportl.gui.compose.layout

object Alignment {

    // Vertical Alignments
    val Top: Vertical = VerticalImpl(-1)
    val CenterVertically: Vertical = VerticalImpl(0)
    val Bottom: Vertical = VerticalImpl(1)

    // Horizontal Alignments
    val Start: Horizontal = HorizontalImpl(-1)
    val CenterHorizontally: Horizontal = HorizontalImpl(0)
    val End: Horizontal = HorizontalImpl(1)

    // Combined 2D Alignments
    val TopStart: HorizontalAndVertical = Top + Start
    val TopCenter: HorizontalAndVertical = Top + CenterHorizontally
    val TopEnd: HorizontalAndVertical = Top + End

    val CenterStart: HorizontalAndVertical = CenterVertically + Start
    val Center: HorizontalAndVertical = CenterVertically + CenterHorizontally
    val CenterEnd: HorizontalAndVertical = CenterVertically + End

    val BottomStart: HorizontalAndVertical = Bottom + Start
    val BottomCenter: HorizontalAndVertical = Bottom + CenterHorizontally
    val BottomEnd: HorizontalAndVertical = Bottom + End

    /**
     * Calculates the position of an item of a certain width within an available width.
     * The [LayoutDirection] may be used to mirror the alignment.
     */
    fun interface Horizontal {

        fun align(size: Size, space: Size, layoutDirection: LayoutDirection): Size

        operator fun plus(vertical: Vertical): HorizontalAndVertical = HorizontalAndVerticalImpl(this, vertical)

    }

    /**
     * Calculates the position of an item of a certain height within an available height.
     */
    fun interface Vertical {

        fun align(size: Size, space: Size): Size

        operator fun plus(horizontal: Horizontal): HorizontalAndVertical = HorizontalAndVerticalImpl(horizontal, this)

    }

    /**
     * Calculates the position of an item area within a container area.
     */
    fun interface HorizontalAndVertical {

        fun align(size: Offset, space: Offset, layoutDirection: LayoutDirection): Offset

    }

    private class HorizontalImpl(val bias: Int) : Horizontal {

        override fun align(
            size: Size,
            space: Size,
            layoutDirection: LayoutDirection,
        ): Size {
            val center = (space - size) / 2
            val resolvedBias = if (layoutDirection == LayoutDirection.LtR) bias else -1 * bias
            return center * (1 + resolvedBias)
        }

    }

    private class VerticalImpl(val bias: Int) : Vertical {

        override fun align(
            size: Size,
            space: Size,
        ): Size {
            val center = (space - size) / 2
            return center * (1 + bias)
        }

    }

    private class HorizontalAndVerticalImpl(val horizontal: Horizontal, val vertical: Vertical) : HorizontalAndVertical {

        override fun align(
            size: Offset,
            space: Offset,
            layoutDirection: LayoutDirection,
        ): Offset {
            return Offset(
                horizontal.align(size.x, space.x, layoutDirection),
                vertical.align(size.y, space.y)
            )
        }

    }

}