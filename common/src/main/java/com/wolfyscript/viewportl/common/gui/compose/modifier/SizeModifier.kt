package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.*
import com.wolfyscript.viewportl.gui.compose.modifier.*

class SizeModifier(
    val minWidth: Dp = Dp.Unspecified,
    val minHeight: Dp = Dp.Unspecified,
    val maxWidth: Dp = Dp.Unspecified,
    val maxHeight: Dp = Dp.Unspecified,
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
    val minWidth: Dp = Dp.Unspecified,
    val minHeight: Dp = Dp.Unspecified,
    val maxWidth: Dp = Dp.Unspecified,
    val maxHeight: Dp = Dp.Unspecified,
    val enforceIncoming: Boolean,
) : LayoutModifierNode, ModifierNode {

    private fun targetConstraints(outerMaxWidth: Dp, outerMaxHeight: Dp): Constraints {
        val maxWidth: Dp = if (maxWidth.isSpecified) {
            maxWidth.value.coerceAtLeast(0f).dp
        } else {
            outerMaxWidth
        }

        val maxHeight: Dp = if (maxHeight.isSpecified) {
            maxHeight.value.coerceAtLeast(0f).dp
        } else {
            outerMaxHeight
        }

        val minWidth: Dp = if (minWidth.isSpecified) {
            minWidth.value.coerceIn(0f, maxWidth.value).dp
        } else {
            Dp.Zero
        }

        val minHeight: Dp = if (minHeight.isSpecified) {
            minHeight.value.coerceIn(0f, maxHeight.value).dp
        } else {
            Dp.Zero
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
    val minWidth: Dp = Dp.Unspecified,
    val minHeight: Dp = Dp.Unspecified,
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
    internal var minWidth: Dp = Dp.Unspecified,
    internal var minHeight: Dp = Dp.Unspecified,
) : ModifierNode, LayoutModifierNode {

    override fun onAttach() {}

    override fun onDetach() {}

    override fun LayoutModifyScope.modify(constraints: Constraints): LayoutModification {
        val modifiedConstraints = Constraints(
            minWidth = if (minWidth.isSpecified) {
                if (constraints.minWidth.value == 0f) {
                    minWidth.value.coerceIn(0f, constraints.maxWidth.value).dp
                } else {
                    constraints.minWidth
                }
            } else {
                constraints.minWidth
            },
            maxWidth = constraints.maxWidth,
            minHeight = if (minHeight.isSpecified) {
                Dp(
                    if (constraints.minHeight.value == 0f) {
                        minHeight.value.coerceIn(0f, constraints.maxHeight.value)
                    } else {
                        constraints.minHeight.value
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

        val minWidth = if (direction.isHorizontal) {
            (constraints.maxWidth * correctFraction).coerceIn(constraints.minWidth, constraints.maxWidth)
        } else {
            constraints.minWidth
        }

        val minHeight = if (direction.isVertical) {
            (constraints.maxHeight * correctFraction).coerceIn(constraints.minHeight, constraints.maxHeight)
        } else {
            constraints.minHeight
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
