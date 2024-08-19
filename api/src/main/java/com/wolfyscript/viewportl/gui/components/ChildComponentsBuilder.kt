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

interface ChildComponentsBuilder<out T> {

    fun group(id: String? = null, groupConfigurator: ReceiverConsumer<ComponentGroup>) {
        component(id, ComponentGroup::class.java, groupConfigurator)
    }

    fun button(id: String? = null, buttonConfigurator: ReceiverConsumer<Button>) {
        component(id, Button::class.java, buttonConfigurator)
    }

    fun slot(id: String? = null, slotBtnConfigurator: ReceiverConsumer<StackInputSlot>) {
        component(id, StackInputSlot::class.java, slotBtnConfigurator)
    }

    /**
     *
     *
     * Renders the specified component with the given id.
     *
     *
     * @param id              The id of the component to render
     * @param type     The type of the builder to use
     * @param configurator The consumer to configure the builder
     * @param <B>             The type of the component builder
     * @return This Builder for chaining
    </B> */
    fun <B : Component> component(id: String? = null, type: Class<B>, configurator: ReceiverConsumer<B>)

}

inline fun <reified B : Component> ChildComponentsBuilder<Any>.component(id: String? = null, configurator: ReceiverConsumer<B>) = component(id, B::class.java, configurator)
