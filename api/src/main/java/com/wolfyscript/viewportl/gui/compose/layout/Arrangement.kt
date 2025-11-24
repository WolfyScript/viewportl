package com.wolfyscript.viewportl.gui.compose.layout

object Arrangement {

    interface Horizontal {

        /**
         * Arranges items horizontally
         *
         * @param totalWidth The maximum width space available for the items
         * @param layoutDirection A layout direction ([LtR][LayoutDirection.LtR], [RtL][LayoutDirection.RtL]) from the parent that should be taken into account.
         * @param itemSizes An array of sizes representing the items
         *
         * @return An array of item positions in the same order and of the same size as [itemSizes]
         */
        fun arrange(totalWidth: Size, layoutDirection: LayoutDirection, itemSizes: Array<Size>): Array<Size>

    }

    interface Vertical {

        fun arrange(totalHeight: Size, sizes: Array<Size>): Array<Size>

    }

    interface HorizontalOrVertical : Horizontal, Vertical {}

    /**
     * Arranges items as close as possible to the beginning of the horizontal axis.
     * The beginning depends on the [LayoutDirection] ([LtR][LayoutDirection.LtR] or [RtL][LayoutDirection.RtL])
     *
     * For example for 3 items:
     *
     * **LtR**: `123######`
     *
     * **RtL**: `######321`
     *
     */
    object Start : Horizontal {

        override fun arrange(
            totalWidth: Size,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Size>,
        ): Array<Size> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeConcatStart(itemSizes, false)
            } else {
                placeConcatEnd(totalWidth, itemSizes, true)
            }
        }

    }

    /**
     * Arranges items as close as possible to the end of the horizontal axis.
     * The end depends on the [LayoutDirection] ([LtR][LayoutDirection.LtR] or [RtL][LayoutDirection.RtL])
     *
     * For example for 3 items:
     *
     * **LtR**: `######123`
     *
     * **RtL**: `321######`
     *
     */
    object End : Horizontal {

        override fun arrange(
            totalWidth: Size,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Size>,
        ): Array<Size> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeConcatEnd(totalWidth, itemSizes, false)
            } else {
                placeConcatStart(itemSizes, true)
            }
        }

    }

    /**
     *
     */
    object Top : Vertical {

        override fun arrange(
            totalHeight: Size,
            sizes: Array<Size>,
        ): Array<Size> = placeConcatStart(sizes, false)

    }

    /**
     *
     */
    object Bottom : Vertical {

        override fun arrange(
            totalHeight: Size,
            sizes: Array<Size>,
        ): Array<Size> = placeConcatEnd(totalHeight, sizes, false)

    }

    /**
     *
     */
    object Center : HorizontalOrVertical {

        override fun arrange(
            totalWidth: Size,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Size>,
        ): Array<Size> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeCenter(totalWidth, itemSizes, false)
            } else {
                placeCenter(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Size,
            sizes: Array<Size>,
        ): Array<Size> = placeCenter(totalHeight, sizes, false)

    }

    /**
     *
     */
    object SpaceEvenly: HorizontalOrVertical {

        override fun arrange(
            totalWidth: Size,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Size>,
        ): Array<Size> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeSpaceEvenly(totalWidth, itemSizes, false)
            } else {
                placeSpaceEvenly(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Size,
            sizes: Array<Size>,
        ): Array<Size> = placeSpaceEvenly(totalHeight, sizes, false)

    }

    /**
     *
     */
    object SpaceBetween: HorizontalOrVertical {

        override fun arrange(
            totalWidth: Size,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Size>,
        ): Array<Size> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeSpaceBetween(totalWidth, itemSizes, false)
            } else {
                placeSpaceBetween(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Size,
            sizes: Array<Size>,
        ): Array<Size> = placeSpaceBetween(totalHeight, sizes, false)

    }

    /**
     *
     */
    object SpaceAround : HorizontalOrVertical {

        override fun arrange(
            totalWidth: Size,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Size>,
        ): Array<Size> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeSpaceAround(totalWidth, itemSizes, false)
            } else {
                placeSpaceAround(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Size,
            sizes: Array<Size>,
        ): Array<Size> = placeSpaceAround(totalHeight, sizes, false)

    }

    internal fun placeConcatEnd(totalSize: Size, sizes: Array<Size>, reverseItems: Boolean): Array<Size> {
        val occupiedSize = sizes.fold(Size.Zero) { acc, size -> acc + size }
        var currentPos = totalSize - occupiedSize
        val out = Array(sizes.size) { Size.Zero }
        sizes.forEachIndexed(reverseItems) { index, size ->
            out[index] = currentPos
            currentPos += size
        }
        return out
    }

    internal fun placeConcatStart(sizes: Array<Size>, reverseItems: Boolean): Array<Size> {
        var totalSize = Size.Zero
        val out = Array(sizes.size) { Size.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = totalSize
            totalSize += itemSize
        }
        return out
    }

    internal fun placeCenter(totalSize: Size, sizes: Array<Size>, reverseItems: Boolean): Array<Size> {
        val occupiedSize = sizes.fold(Size.Zero) { acc, size -> acc + size }
        var currentPos = (totalSize - occupiedSize) / 2
        val out = Array(sizes.size) { Size.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = currentPos
            currentPos += itemSize
        }
        return out
    }

    internal fun placeSpaceEvenly(totalSize: Size, sizes: Array<Size>, reverseItems: Boolean): Array<Size> {
        val occupiedSize = sizes.fold(Size.Zero) { acc, size -> acc + size }
        val gapSize = (totalSize - occupiedSize) / (sizes.size + 1)
        var currentPos = gapSize
        val out = Array(sizes.size) { Size.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = totalSize
            currentPos += itemSize + gapSize
        }
        return out
    }

    internal fun placeSpaceBetween(totalSize: Size, sizes: Array<Size>, reverseItems: Boolean): Array<Size> {
        if (sizes.isEmpty()) return emptyArray()
        val occupiedSize = sizes.fold(Size.Zero) { acc, size -> acc + size }
        val gaps = maxOf(sizes.lastIndex, 1)
        val gapSize = (totalSize - occupiedSize) / gaps

        var currentPos = if (reverseItems && sizes.size == 1) gapSize else Size.Zero
        val out = Array(sizes.size) { Size.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = currentPos
            currentPos += itemSize + gapSize
        }
        return out
    }

    internal fun placeSpaceAround(totalSize: Size, sizes: Array<Size>, reverseItems: Boolean): Array<Size> {
        val occupiedSize = sizes.fold(Size.Zero) { acc, size -> acc + size }
        val gapSize = if (sizes.isEmpty()) {
            Size.Zero
        } else {
            (totalSize - occupiedSize) / sizes.size
        }
        var currentPos = gapSize / 2
        val out = Array(sizes.size) { Size.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = currentPos
            currentPos += itemSize + gapSize
        }
        return out
    }

    private inline fun <T> Array<T>.forEachIndexed(reversed: Boolean, action: (Int, T) -> Unit) {
        if (!reversed) {
            forEachIndexed(action)
        } else {
            for (i in (size - 1) downTo 0) {
                action(i, get(i))
            }
        }
    }

}