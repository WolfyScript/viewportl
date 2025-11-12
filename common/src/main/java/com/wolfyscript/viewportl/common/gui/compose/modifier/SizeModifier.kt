package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.*
import com.wolfyscript.viewportl.gui.compose.modifier.*

class SizeModifier(
    val minWidth: Size = Size.Unspecified,
    val minHeight: Size = Size.Unspecified,
    val maxWidth: Size = Size.Unspecified,
    val maxHeight: Size = Size.Unspecified,
    val enforceIncoming: Boolean,
) : ModifierData<SizeModifierNode> {

    override fun create(): SizeModifierNode {
        return SizeModifierNode(
            minWidth, minHeight, maxWidth, maxHeight, enforceIncoming
        )
    }

    override fun update(node: SizeModifierNode) {

    }

}

class SizeModifierNode(
    val minWidth: Size = Size.Unspecified,
    val minHeight: Size = Size.Unspecified,
    val maxWidth: Size = Size.Unspecified,
    val maxHeight: Size = Size.Unspecified,
    val enforceIncoming: Boolean,
) : LayoutModifierNode, ModifierNode {

    private fun targetConstraints(outerMaxWidth: Size, outerMaxHeight: Size): Constraints {
        val maxWidth: Size = if (maxWidth.isSpecified) {
            maxWidth.slot.value.coerceAtLeast(0).slots or maxWidth.dp.value.coerceAtLeast(0).dp
        } else {
            outerMaxWidth
        }

        val maxHeight: Size = if (maxHeight.isSpecified) {
            maxHeight.slot.value.coerceAtLeast(0).slots or maxHeight.dp.value.coerceAtLeast(0).dp
        } else {
            outerMaxHeight
        }

        val minWidth = if (minWidth.isSpecified) {
            minWidth.slot.value.coerceIn(0, maxWidth.slot.value).slots or minWidth.dp.value.coerceIn(
                0,
                maxWidth.dp.value
            ).dp
        } else {
            Size.Zero
        }

        val minHeight = if (minHeight.isSpecified) {
            minHeight.slot.value.coerceIn(0, maxHeight.slot.value).slots or minHeight.dp.value.coerceIn(
                0,
                maxHeight.dp.value
            ).dp
        } else {
            Size.Zero
        }

        return Constraints(
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            minWidth = minWidth,
            minHeight = minHeight,
        )
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

    override fun LayoutModifyScope.modify(constraints: Constraints): LayoutModification {
        val target = targetConstraints(constraints.maxWidth, constraints.maxHeight)
        val modifiedConstraints: Constraints = if (enforceIncoming) {
            constraints.constrain(target)
        } else {
            val resolvedMinWidth = if (minWidth.isSpecified) {
                target.minWidth
            } else {
                constraints.minWidth
            }
            val resolvedMaxWidth = if (maxWidth.isSpecified) {
                target.maxWidth
            } else {
                constraints.maxWidth
            }
            val resolvedMinHeight = if (minHeight.isSpecified) {
                target.minHeight
            } else {
                constraints.minHeight
            }
            val resolvedMaxHeight = if (maxHeight.isSpecified) {
                target.maxHeight
            } else {
                constraints.maxHeight
            }

            Constraints(
                resolvedMinWidth,
                resolvedMaxWidth,
                resolvedMinHeight,
                resolvedMaxHeight
            )
        }
        return modifyLayout(modifiedConstraints) { it }
    }

}
