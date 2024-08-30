/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.function.Supplier

/**
 * A simple button that can execute actions when clicked and change its icon.
 *
 * ###### Sound
 * By default, it uses the default Button sound from Minecraft, on interaction.
 * While the sound can be removed, it is highly recommended to play a sound on interaction to notify the player that an action has been recognised!
 *
 * ###### Native Component
 * Buttons are a native component meaning they have a platform specific implementation that handles both interactions and rendering.
 * Only native components will be present in the component graph, non-native components don't really exist, they just group native components together.
 */
fun button(
    runtime: ViewRuntime,
    icon: ButtonIcon.() -> Unit,
    styles: RenderProperties.() -> Unit,
    sound: Sound? = null,
    onClick: ClickTransaction.() -> Unit = {}
) = component(runtime) {
    runtime.viewportl.guiFactory.componentFactory.button(
        ButtonProperties(
            runtime,
            icon,
            styles,
            sound,
            onClick
        )
    )
}

/**
 * The properties used to create a button implementation
 */
data class ButtonProperties(
    val runtime: ViewRuntime,
    val icon: ButtonIcon.() -> Unit,
    val styles: RenderProperties.() -> Unit,
    val sound: Sound? = null,
    val onClick: ClickTransaction.() -> Unit
)

/**
 * A simple button that has an icon (ItemStack) and an interaction callback.
 * It always has a 1x1 size, because it occupies a single slot.
 *
 * This is a native component that will be present in the data model and has native implementations on each platform, that handle the rendering and interaction.
 */
interface Button : NativeComponent {
    var sound: Sound?
    var icon: ButtonIcon
    var onClick: ReceiverConsumer<ClickTransaction>?
}

interface ButtonIcon {

    var stack: ItemStackConfig

    fun stack(itemId: String, stackConfig: ReceiverConsumer<ItemStackConfig>)

    var resolvers: TagResolver

    fun resolvers(resolverSupplier: Supplier<TagResolver>)

}



