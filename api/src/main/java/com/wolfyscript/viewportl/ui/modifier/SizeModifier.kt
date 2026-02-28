package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Direction
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.coerceIn
import com.wolfyscript.viewportl.ui.layout.constrain
import com.wolfyscript.viewportl.ui.layout.dp
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.width(width: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width
    )
)

fun ModifierStackBuilder.height(height: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = height,
        maxHeight = height
    )
)

fun ModifierStackBuilder.size(size: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = size,
        minHeight = size,
        maxWidth = size,
        maxHeight = size
    )
)

fun ModifierStackBuilder.size(width: Dp, height: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        minHeight = height,
        maxHeight = height
    )
)

fun ModifierStackBuilder.widthIn(min: Dp, max: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = min,
        maxWidth = max,
    )
)

fun ModifierStackBuilder.heightIn(min: Dp, max: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = min,
        maxHeight = max,
    )
)

fun ModifierStackBuilder.sizeIn(
    minWidth: Dp,
    minHeight: Dp,
    maxWidth: Dp,
    maxHeight: Dp,
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
    )
)

fun ModifierStackBuilder.requireWidth(width: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireHeight(height: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = height,
        maxHeight = height,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireSize(size: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = size,
        minHeight = size,
        maxWidth = size,
        maxHeight = size,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireSize(width: Dp, height: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        minHeight = height,
        maxHeight = height,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireWidthIn(min: Dp, max: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = min,
        maxWidth = max,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireHeightIn(min: Dp, max: Dp) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = min,
        maxHeight = max,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireSizeIn(
    minWidth: Dp,
    minHeight: Dp,
    maxWidth: Dp,
    maxHeight: Dp,
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
        enforceIncoming = false
    )
)

/**
 * Specifies a minimum [width][minWidth] or [height][minHeight],
 * that applies when the incoming [minWidth][com.wolfyscript.viewportl.ui.layout.Constraints.minWidth]/[minHeight][com.wolfyscript.viewportl.ui.layout.Constraints.minHeight] is `0`.
 */
fun ModifierStackBuilder.defaultMinSize(minWidth: Dp = Dp.Unspecified, minHeight: Dp = Dp.Unspecified) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createDefaultMinSizeModifier(minWidth, minHeight)
)

fun ModifierStackBuilder.fillMaxWidth(fraction: Float = 1f) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createFillModifier(Direction.Horizontal, fraction)
)

fun ModifierStackBuilder.fillMaxHeight(fraction: Float = 1f) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createFillModifier(Direction.Vertical, fraction)
)

fun ModifierStackBuilder.fillMaxSize(fraction: Float = 1f) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createFillModifier(Direction.Both, fraction)
)

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
