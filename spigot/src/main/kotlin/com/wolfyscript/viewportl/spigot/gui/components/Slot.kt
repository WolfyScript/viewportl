package com.wolfyscript.viewportl.spigot.gui.components

import com.wolfyscript.viewportl.common.gui.components.SlotImpl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.SlotProperties
import com.wolfyscript.viewportl.gui.reactivity.ReadOnlySignal

fun setupSlot(properties: SlotProperties) {
    val runtime = properties.runtime.into()
    val reactiveSource = runtime.reactiveSource
    val buildContext = runtime.buildContext

    val slot = SlotImpl(
        "",
        runtime.viewportl,
        buildContext,
        buildContext.currentParent,
        onValueChange = properties.onValueChange,
        onClick = properties.onClick,
        onDrag = properties.onDrag,
        canPickUpStack = properties.canPickUpStack,
        value = properties.value
    )

    val id = buildContext.addComponent(slot)

    if (properties.value is ReadOnlySignal<*>) {
        reactiveSource.createEffect {
            slot.value = properties.value
        }
    }

}