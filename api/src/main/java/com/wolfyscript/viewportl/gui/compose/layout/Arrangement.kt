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
        fun arrange(totalWidth: Dp, layoutDirection: LayoutDirection, itemSizes: Array<Dp>): Array<Dp>

    }

    interface Vertical {

        fun arrange(totalHeight: Dp, sizes: Array<Dp>): Array<Dp>

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
            totalWidth: Dp,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Dp>,
        ): Array<Dp> {
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
            totalWidth: Dp,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Dp>,
        ): Array<Dp> {
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
            totalHeight: Dp,
            sizes: Array<Dp>,
        ): Array<Dp> = placeConcatStart(sizes, false)

    }

    /**
     *
     */
    object Bottom : Vertical {

        override fun arrange(
            totalHeight: Dp,
            sizes: Array<Dp>,
        ): Array<Dp> = placeConcatEnd(totalHeight, sizes, false)

    }

    /**
     *
     */
    object Center : HorizontalOrVertical {

        override fun arrange(
            totalWidth: Dp,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Dp>,
        ): Array<Dp> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeCenter(totalWidth, itemSizes, false)
            } else {
                placeCenter(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Dp,
            sizes: Array<Dp>,
        ): Array<Dp> = placeCenter(totalHeight, sizes, false)

    }

    /**
     *
     */
    object SpaceEvenly: HorizontalOrVertical {

        override fun arrange(
            totalWidth: Dp,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Dp>,
        ): Array<Dp> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeSpaceEvenly(totalWidth, itemSizes, false)
            } else {
                placeSpaceEvenly(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Dp,
            sizes: Array<Dp>,
        ): Array<Dp> = placeSpaceEvenly(totalHeight, sizes, false)

    }

    /**
     *
     */
    object SpaceBetween: HorizontalOrVertical {

        override fun arrange(
            totalWidth: Dp,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Dp>,
        ): Array<Dp> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeSpaceBetween(totalWidth, itemSizes, false)
            } else {
                placeSpaceBetween(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Dp,
            sizes: Array<Dp>,
        ): Array<Dp> = placeSpaceBetween(totalHeight, sizes, false)

    }

    /**
     *
     */
    object SpaceAround : HorizontalOrVertical {

        override fun arrange(
            totalWidth: Dp,
            layoutDirection: LayoutDirection,
            itemSizes: Array<Dp>,
        ): Array<Dp> {
            return if (layoutDirection == LayoutDirection.LtR) {
                placeSpaceAround(totalWidth, itemSizes, false)
            } else {
                placeSpaceAround(totalWidth, itemSizes, true)
            }
        }

        override fun arrange(
            totalHeight: Dp,
            sizes: Array<Dp>,
        ): Array<Dp> = placeSpaceAround(totalHeight, sizes, false)

    }

    internal fun placeConcatEnd(totalSize: Dp, sizes: Array<Dp>, reverseItems: Boolean): Array<Dp> {
        val occupiedSize = sizes.fold(Dp.Zero) { acc, size -> acc + size }
        var currentPos = totalSize - occupiedSize
        val out = Array(sizes.size) { Dp.Zero }
        sizes.forEachIndexed(reverseItems) { index, size ->
            out[index] = currentPos
            currentPos += size
        }
        return out
    }

    internal fun placeConcatStart(sizes: Array<Dp>, reverseItems: Boolean): Array<Dp> {
        var totalSize = Dp.Zero
        val out = Array(sizes.size) { Dp.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = totalSize
            totalSize += itemSize
        }
        return out
    }

    internal fun placeCenter(totalSize: Dp, sizes: Array<Dp>, reverseItems: Boolean): Array<Dp> {
        val occupiedSize = sizes.fold(Dp.Zero) { acc, size -> acc + size }
        var currentPos = (totalSize - occupiedSize) / 2
        val out = Array(sizes.size) { Dp.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = currentPos
            currentPos += itemSize
        }
        return out
    }

    internal fun placeSpaceEvenly(totalSize: Dp, sizes: Array<Dp>, reverseItems: Boolean): Array<Dp> {
        val occupiedSize = sizes.fold(Dp.Zero) { acc, size -> acc + size }
        val gapSize = (totalSize - occupiedSize) / (sizes.size + 1)
        var currentPos = gapSize // 1.5
        val out = Array(sizes.size) { Dp.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = currentPos
            currentPos += itemSize + gapSize
        }
        return out

        // 1.5, 4, 6.5
        // [] [] [x] [] [x] [] [] [x] []
    }

    internal fun placeSpaceBetween(totalSize: Dp, sizes: Array<Dp>, reverseItems: Boolean): Array<Dp> {
        if (sizes.isEmpty()) return emptyArray()
        val occupiedSize = sizes.fold(Dp.Zero) { acc, size -> acc + size }
        val gaps = maxOf(sizes.lastIndex, 1)
        val gapSize = (totalSize - occupiedSize) / gaps

        var currentPos = if (reverseItems && sizes.size == 1) gapSize else Dp.Zero
        val out = Array(sizes.size) { Dp.Zero }
        sizes.forEachIndexed(reverseItems) { index, itemSize ->
            out[index] = currentPos
            currentPos += itemSize + gapSize
        }
        return out
    }

    internal fun placeSpaceAround(totalSize: Dp, sizes: Array<Dp>, reverseItems: Boolean): Array<Dp> {
        val occupiedSize = sizes.fold(Dp.Zero) { acc, size -> acc + size }
        val gapSize = if (sizes.isEmpty()) {
            Dp.Zero
        } else {
            (totalSize - occupiedSize) / sizes.size
        }
        var currentPos = gapSize / 2
        val out = Array(sizes.size) { Dp.Zero }
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