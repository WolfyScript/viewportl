package com.wolfyscript.viewportl.spigot.gui.components

import com.wolfyscript.viewportl.common.gui.WindowImpl
import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.reactivity.ReadOnlySignal
import java.lang.IllegalStateException

internal fun setupButton(properties: ButtonProperties) {
    val runtime = properties.runtime
    val reactiveSource = runtime.into().reactiveSource
    val buildContext = (runtime.currentMenu as? WindowImpl)?.context ?: throw IllegalStateException("Cannot create button outside of window")

    val button = ButtonImpl(
        "",
        runtime.viewportl,
        buildContext,
        buildContext.parent,
        onClick = properties.onClick,
        sound = properties.sound,
    )

    // Add the button once on init
    val id = runtime.into().modelGraph.addNode(button)

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
