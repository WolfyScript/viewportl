package com.wolfyscript.viewportl.common.gui.elements

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.inject.Inject
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.data.DataKeyProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.ButtonIcon
import com.wolfyscript.viewportl.gui.elements.ButtonProperties
import com.wolfyscript.viewportl.gui.elements.DynamicProperty
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.elements.ElementImplementation
import com.wolfyscript.viewportl.gui.interaction.ClickInfo
import com.wolfyscript.viewportl.gui.reactivity.ReadOnlySignal
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
    // Apply initial button settings
    properties.icon(button.icon)
    properties.styles(button.styles)

    // Add the button once on init
    val id = (properties.scope as ComponentScopeImpl).setComponent(button)

    // Setup effects to update button. This way signals only update the parts that actually require updates
    if (properties.sound is ReadOnlySignal<*>) {
        reactiveSource.createEffect {
            button.sound = properties.sound
        }
    }
    reactiveSource.createEffect {
        val icon = DynamicIcon(runtime)
        properties.icon(icon)
        button.icon = icon
    }
    reactiveSource.createEffect {
        properties.styles(button.styles)
    }
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

    override var stack: ItemStackConfig = runtime.viewportl.scafall.factories.itemsFactory.createStackConfig(Key.key(Key.MINECRAFT_NAMESPACE, "air"))

    override fun stack(itemId: String, stackConfig: ItemStackConfig.(DataKeyProvider) -> Unit) {
        val newStack = runtime.viewportl.scafall.factories.itemsFactory.createStackConfig(Key.key(Key.MINECRAFT_NAMESPACE, itemId))
        newStack.stackConfig(ScafallProvider.get().factories.itemsFactory.dataKeyProvider)
        stack = newStack
    }

}
