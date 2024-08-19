/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import net.kyori.adventure.sound.Sound

/**
 * A simple button that has an icon (ItemStack) and an interaction callback.
 * It always has a 1x1 size, because it occupies a single slot.
 *
 */
interface Button : Component {
    override fun width(): Int {
        return 1
    }

    override fun height(): Int {
        return 1
    }

    var sound: Sound?

    var icon: ButtonIcon

    var onClick: ReceiverConsumer<ClickTransaction>?

    fun onClick(consumer: ReceiverConsumer<ClickTransaction>) {
        onClick = consumer
    }

    fun icon(iconConsumer: ReceiverConsumer<ButtonIcon>)
}
