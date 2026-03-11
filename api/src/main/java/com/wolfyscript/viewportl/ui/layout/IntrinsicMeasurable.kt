package com.wolfyscript.viewportl.ui.layout

import com.wolfyscript.viewportl.ui.IntrinsicSize

interface IntrinsicMeasurable {

    val scopeData: Any?

    fun minIntrinsicWidth(height: Dp): Dp

    fun maxIntrinsicWidth(height: Dp): Dp

    fun minIntrinsicHeight(width: Dp): Dp

    fun maxIntrinsicHeight(width: Dp): Dp

    fun mainAxisIntrinsicSize(orientation: LayoutOrientation, mainAxisSize: IntrinsicSize, crossAxisSize: Dp): Dp {
        return when (orientation) {
            LayoutOrientation.Horizontal -> if (mainAxisSize == IntrinsicSize.Min) {
                minIntrinsicWidth(crossAxisSize)
            } else {
                maxIntrinsicWidth(crossAxisSize)
            }

            LayoutOrientation.Vertical -> if (mainAxisSize == IntrinsicSize.Min) {
                minIntrinsicHeight(crossAxisSize)
            } else {

                maxIntrinsicHeight(crossAxisSize)
            }
        }
    }

    fun crossAxisIntrinsicSize(orientation: LayoutOrientation, crossAxisSize: IntrinsicSize, mainAxisSize: Dp): Dp {
        return when (orientation) {
            LayoutOrientation.Horizontal -> if (crossAxisSize == IntrinsicSize.Min) {
                minIntrinsicHeight(mainAxisSize)
            } else {
                maxIntrinsicHeight(mainAxisSize)
            }

            LayoutOrientation.Vertical -> if (crossAxisSize == IntrinsicSize.Min) {
                minIntrinsicWidth(mainAxisSize)
            } else {
                maxIntrinsicWidth(mainAxisSize)
            }
        }
    }

}