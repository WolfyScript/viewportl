package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import kotlin.reflect.KClass

/**
 * Lightweight Modifier information used to [create] the underlying [ModifierNode]
 */
interface ModifierData<N: ModifierNode> {

    /**
     * Creates a [ModifierNode] using the contained data.
     */
    fun create(): N

    fun update(node: N)

}

/**
 * The actual stateful Modifier used to modify the behaviour and appearance of a [com.wolfyscript.viewportl.gui.compose.Node]
 */
interface ModifierNode {

    fun onAttach()

    fun onDetach()

}

/**
 * A stack of [ModifierNodes][ModifierNode] applied to a [com.wolfyscript.viewportl.gui.compose.Node]
 */
interface ModifierStack {

    fun modifyLayout(nodeConstraints: Constraints): LayoutModification

    fun <T: ModifierNode> firstOfType(nodeType: KClass<T>): T?

}

/**
 * Used to build the [ModifierStack] using the provided functions.
 *
 * Custom Modifiers may provide an extension function for this scope to wrap [push].
 */
interface ModifierStackScope {

    fun push(modifier: ModifierData<*>)

}

/**
 * Callback that builds the [ModifierStack]
 */
fun interface ModifierStackBuilder {

    fun ModifierStackScope.build()

}