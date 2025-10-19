package com.wolfyscript.viewportl.common.gui.elements

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.elements.*
import net.kyori.adventure.sound.Sound

internal fun setupButton(properties: ButtonProperties): Button {
    val button = ButtonImpl(
        "",
        icon = properties.icon,
        onClick = properties.onClick,
        sound = properties.sound,
    )
    return button
}

@ElementImplementation(base = Button::class)
class ButtonImpl(
    id: String,
    override var icon: () -> ItemStackSnapshot,
    override var onClick: () -> Unit,
    override var sound: Sound?
) : AbstractElementImpl<Button>(id), Button
