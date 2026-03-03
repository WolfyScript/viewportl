package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.ui.IntrinsicDimension
import com.wolfyscript.viewportl.ui.IntrinsicSize
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.viewportl
import kotlin.reflect.KClass

/**
 * Lightweight Modifier information used to [create] the underlying [ModifierNode]
 */
interface ModifierData<N : ModifierNode> {

    /**
     * Creates a [ModifierNode] using the contained data.
     */
    fun create(): N

    fun update(node: N)

}

/**
 * The actual stateful Modifier used to modify the behaviour and appearance of a [com.wolfyscript.viewportl.ui.Node]
 */
interface ModifierNode {

    fun onAttach()

    fun onDetach()

    fun onMeasurementsChanged() {}

    fun onLayoutChanged() {}

}

/**
 * A stack of [ModifierNodes][ModifierNode] applied to a [com.wolfyscript.viewportl.ui.Node]
 */
interface ModifierStack {

    /**
     * Modifies the incoming [nodeConstraints] using the [Modifiers][ModifierNode] in this stack.
     *
     * @return The [LayoutModification] accumulated from the [LayoutModifierNode]s in this stack.
     */
    fun modifyMeasure(nodeConstraints: Constraints): LayoutModification

    /**
     * Modifies the layout (outgoing [Measurements][com.wolfyscript.viewportl.ui.layout.Measurements]) using the calculated size and offset instructions from [modifyMeasure].
     *
     * When not yet measured by [modifyMeasure], returns the [initialMeasure] as is.
     */
    fun modifyLayout(initialMeasure: MeasureModification): MeasureModification

    /**
     *
     * @param layoutDirection the layout direction to use for measuring
     * @param crossAxisSize the initial incoming cross axis size, e.g. height or width
     * @param modificationFn the method to use to modify the incoming crossAxisSize
     * @param nodeIntrinsics calculates the [com.wolfyscript.viewportl.ui.layout.IntrinsicMeasurable] intrinsics
     */
    fun modifyIntrinsic(
        intrinsicSize: IntrinsicSize,
        intrinsicDimension: IntrinsicDimension,
        crossAxisSize: Dp,
        nodeIntrinsics: IntrinsicMeasureBlock,
    ): Dp

    fun <T : ModifierNode> firstOfType(nodeType: KClass<T>): T?

    fun <T : ModifierNode> forEachOfType(nodeType: KClass<T>, block: (T) -> Unit)

}

fun interface IntrinsicMeasureBlock {

    operator fun invoke(
        dimension: IntrinsicDimension,
        intrinsicSize: IntrinsicSize,
        crossAxisSize: Dp,
    ): Dp
}

val Modifier
    get() = ScafallProvider.get().viewportl.guiFactory.modifierFactory.createModifierStackBuilder()

/**
 * Callback that builds the [ModifierStack]
 */
interface ModifierStackBuilder {

    fun push(modifier: ModifierData<*>): ModifierStackBuilder

    val data: List<ModifierData<*>>

}