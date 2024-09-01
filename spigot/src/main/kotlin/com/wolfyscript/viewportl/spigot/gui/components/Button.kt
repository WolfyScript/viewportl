package com.wolfyscript.viewportl.spigot.gui.components

import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.components.DynamicIcon
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.reactivity.ReadOnlySignal

internal fun setupButton(properties: ButtonProperties) {
    val runtime = properties.runtime.into()
    val reactiveSource = runtime.reactiveSource
    val buildContext = runtime.buildContext

    val button = ButtonImpl(
        "",
        runtime.viewportl,
        buildContext,
        buildContext.currentParent,
        icon = DynamicIcon(buildContext),
        onClick = properties.onClick,
        sound = properties.sound,
    )
    // Apply initial button settings
    properties.icon(button.icon)
    properties.styles(button.styles)

    // Add the button once on init
    val id = buildContext.addComponent(button)

    // Setup effects to update button. This way signals only update the parts that actually require updates
    if (properties.sound is ReadOnlySignal<*>) {
        reactiveSource.createEffect {
            button.sound = properties.sound
        }
    }
    reactiveSource.createEffect {
        properties.icon(button.icon)
    }
    reactiveSource.createEffect {
        properties.styles(button.styles)
    }
}
