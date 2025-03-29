package com.wolfyscript.viewportl.common.gui.elements

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.inject.Inject
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.common.gui.reactivity.effect.EffectImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.interaction.ClickInfo
import com.wolfyscript.viewportl.gui.reactivity.Signal
import net.kyori.adventure.sound.Sound

internal fun setupButton(properties: ButtonProperties) {
    val runtime = properties.scope.runtime.into()
    val reactiveSource = runtime.reactiveSource

    val button = ButtonImpl(
        "",
        runtime = runtime,
        parent = properties.scope.parent?.component,
        icon = DynamicIcon(runtime),
        onClick = properties.onClick,
        sound = properties.sound,
    )
    // Setup effects to update button. This way signals only update the parts that actually require updates
    if (properties.sound is Signal<*>) {
        val soundEffect = reactiveSource.createEffect {
            button.sound = properties.sound
        }
        (soundEffect as EffectImpl).execute()
    }
    val effect = reactiveSource.createEffect {
        val icon = DynamicIcon(runtime)
        properties.icon(icon)
        button.icon = icon
    }
    (effect as EffectImpl).execute()

    val styleEffect = reactiveSource.createEffect {
        properties.styles(button.styles)
    }
    (styleEffect as EffectImpl).execute()

    // Add the button once on init
    val id = (properties.scope as ComponentScopeImpl).setComponent(button)
}

@ElementImplementation(base = Button::class)
@StaticNamespacedKey(key = "button")
class ButtonImpl @JsonCreator @Inject constructor(
    id: String,
    internal val runtime: ViewRuntime<*,*>,
    viewportl: Viewportl = runtime.viewportl,
    parent: Element? = null,
    icon: ButtonIcon = DynamicIcon(runtime),
    onClick: (ClickInfo.() -> Unit)?,
    sound: Sound?
) : AbstractElementImpl<Button>(id, viewportl, parent), Button {

    override var icon: ButtonIcon by DynamicProperty(runtime, icon)
    override var onClick: (ClickInfo.() -> Unit)? by DynamicProperty(runtime, onClick)
    override var sound: Sound? by DynamicProperty(runtime, sound)

}

class DynamicIcon(
    private val runtime: ViewRuntime<*,*>,
) : ButtonIcon {

    override var stack: ItemStack = runtime.viewportl.scafall.factories.itemsFactory.createStack(Key.key(Key.MINECRAFT_NAMESPACE, "air"))

    override fun stack(itemId: String, stackConfig: ItemStack.() -> Unit) {
        val newStack = runtime.viewportl.scafall.factories.itemsFactory.createStack(Key.key(Key.MINECRAFT_NAMESPACE, itemId))
        newStack.stackConfig()
        stack = newStack
    }

}
