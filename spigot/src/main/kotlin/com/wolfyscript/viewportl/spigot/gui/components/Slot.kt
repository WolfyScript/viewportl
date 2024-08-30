package com.wolfyscript.viewportl.spigot.gui.components

import com.wolfyscript.viewportl.common.gui.WindowImpl
import com.wolfyscript.viewportl.common.gui.components.SlotImpl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.SlotProperties
import com.wolfyscript.viewportl.gui.reactivity.ReadOnlySignal
import java.lang.IllegalStateException

fun setupSlot(properties: SlotProperties) {
    val runtime = properties.runtime
    val reactiveSource = runtime.into().reactiveSource
    val buildContext = (runtime.currentMenu as? WindowImpl)?.context ?: throw IllegalStateException("Cannot create button outside of window")

    val slot = SlotImpl(
        "",
        runtime.viewportl,
        buildContext,
        buildContext.parent,
        onValueChange = properties.onValueChange,
        onClick = properties.onClick,
        onDrag = properties.onDrag,
        canPickUpStack = properties.canPickUpStack,
        value = properties.value
    )

    val id = runtime.into().modelGraph.addNode(slot)

    if (properties.value is ReadOnlySignal<*>) {
        reactiveSource.createEffect {
            slot.value = properties.value
        }
    }

}