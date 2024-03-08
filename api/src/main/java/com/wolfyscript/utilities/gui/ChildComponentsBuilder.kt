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

package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.gui.components.ButtonBuilder
import com.wolfyscript.utilities.gui.components.ComponentClusterBuilder
import com.wolfyscript.utilities.gui.components.StackInputSlotBuilder
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer

interface ChildComponentsBuilder<out T> {

    fun group(id: String? = null, builder: ReceiverConsumer<ComponentClusterBuilder>): T {
        return component(id, ComponentClusterBuilder::class.java, builder)
    }

    fun button(id: String? = null, builder: ReceiverConsumer<ButtonBuilder>) : T {
        return component(id, ButtonBuilder::class.java, builder)
    }

    fun slot(id: String? = null, builder: ReceiverConsumer<StackInputSlotBuilder>) : T {
        return component(id, StackInputSlotBuilder::class.java, builder)
    }

    /**
     *
     *
     * Renders the specified component with the given id.
     *
     *
     * @param id              The id of the component to render
     * @param builderType     The type of the builder to use
     * @param builderConsumer The consumer to configure the builder
     * @param <B>             The type of the component builder
     * @return This Builder for chaining
    </B> */
    fun <B : ComponentBuilder<out Component, Component>> component(
        id: String? = null,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): T

}

inline fun <reified B : ComponentBuilder<out Component, Component>> ChildComponentsBuilder<Any>.component(id: String? = null, builderConsumer: ReceiverConsumer<B>) : Any = component(id, B::class.java, builderConsumer)
