package com.wolfyscript.viewportl.ui.layout

interface PlacementScope {

    /**
     * The offset that gets applied to all following [place] calls within this scope.
     */
    var offset: Offset

    fun Placeable.place(x: Dp, y: Dp) {
        this.placeNoOffset(x + offset.x, y + offset.y)
    }

    fun offsetY(y: Dp) {
        offset = Offset(offset.x, offset.y + y)
    }

    fun offsetX(x: Dp) {
        offset = Offset(offset.x + x, offset.y)
    }

    fun offset(offset: Offset) {
        this.offset = offset.plus(offset)
    }

    fun resetOffset() {
        offset = Offset.Zero
    }

}