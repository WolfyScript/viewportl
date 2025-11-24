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
            maxWidth.slot.value.coerceAtLeast(0).slots or
                    maxWidth.dp.value.coerceAtLeast(0).dp
        } else {
            outerMaxWidth
        }

        val maxHeight: Size = if (maxHeight.isSpecified) {
            maxHeight.slot.value.coerceAtLeast(0).slots or
                    maxHeight.dp.value.coerceAtLeast(0).dp
        } else {
            outerMaxHeight
        }

        val minWidth = if (minWidth.isSpecified) {
            minWidth.slot.value.coerceIn(0, maxWidth.slot.value).slots or
                    minWidth.dp.value.coerceIn(0, maxWidth.dp.value).dp
        } else {
            Size.Zero
        }

        val minHeight = if (minHeight.isSpecified) {
            minHeight.slot.value.coerceIn(0, maxHeight.slot.value).slots or
                    minHeight.dp.value.coerceIn(0, maxHeight.dp.value).dp
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

class DefaultMinSizeModifier(
    val minWidth: Size = Size.Unspecified,
    val minHeight: Size = Size.Unspecified,
) : ModifierData<DefaultMinSizeModifierNode> {

    override fun create(): DefaultMinSizeModifierNode {
        return DefaultMinSizeModifierNode(minWidth, minHeight)
    }

    override fun update(node: DefaultMinSizeModifierNode) {
        node.minWidth = minWidth
        node.minHeight = minHeight
    }

}

class DefaultMinSizeModifierNode(
    internal var minWidth: Size = Size.Unspecified,
    internal var minHeight: Size = Size.Unspecified,
) : ModifierNode, LayoutModifierNode {

    override fun onAttach() {}

    override fun onDetach() {}

    override fun LayoutModifyScope.modify(constraints: Constraints): LayoutModification {
        val modifiedConstraints = Constraints(
            minWidth = if (minWidth.isSpecified) {
                Size(
                    if (constraints.minWidth.slot.value == 0) {
                        minWidth.slot.value.coerceIn(0, constraints.maxWidth.slot.value).slots
                    } else {
                        constraints.minWidth.slot
                    },
                    if (constraints.minWidth.dp.value == 0) {
                        minWidth.dp.value.coerceIn(0, constraints.maxWidth.dp.value).dp
                    } else {
                        constraints.minWidth.dp
                    }
                )
            } else {
                constraints.minWidth
            },
            maxWidth = constraints.maxWidth,
            minHeight = if (minHeight.isSpecified) {
                Size(
                    if (constraints.minHeight.slot.value == 0) {
                        minHeight.slot.value.coerceIn(0, constraints.maxHeight.slot.value).slots
                    } else {
                        constraints.minHeight.slot
                    },
                    if (constraints.minHeight.dp.value == 0) {
                        minHeight.dp.value.coerceIn(0, constraints.maxHeight.dp.value).dp
                    } else {
                        constraints.minHeight.dp
                    }
                )
            } else {
                constraints.minHeight
            },
            maxHeight = constraints.maxHeight,
        )
        return modifyLayout(modifiedConstraints) { it }
    }

}

class FillModifierData(
    val direction: Direction,
    val fraction: Float,
) : ModifierData<FillModifierNode> {

    override fun create(): FillModifierNode {
        return FillModifierNode(direction, fraction)
    }

    override fun update(node: FillModifierNode) {
        node.direction = direction
        node.fraction = fraction
    }

}

class FillModifierNode(
    var direction: Direction,
    var fraction: Float,
) : LayoutModifierNode {

    override fun LayoutModifyScope.modify(constraints: Constraints): LayoutModification {
        val correctFraction = fraction.coerceIn(0f, 1f)

        val minWidth = if (direction.isVertical) {
            (constraints.maxWidth * correctFraction).coerceIn(constraints.minWidth, constraints.maxWidth)
        } else {
            constraints.maxWidth
        }

        val minHeight = if (direction.isHorizontal) {
            (constraints.maxHeight * correctFraction).coerceIn(constraints.minHeight, constraints.maxHeight)
        } else {
            constraints.maxHeight
        }

        return modifyLayout(
            modifiedConstraints = Constraints(
                minWidth,
                constraints.maxWidth,
                minHeight,
                constraints.maxHeight
            )
        ) { it }
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

}
