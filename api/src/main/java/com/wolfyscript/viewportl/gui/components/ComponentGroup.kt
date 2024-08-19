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
import java.util.*

interface ComponentGroup : Component, Configurable, ChildComponentsBuilder<ComponentGroup>, ConditionalChildComponentBuilder, MatchChildComponentBuilder {
    /**
     * The children of this Component; or an empty Set if there are no children.
     *
     * @return The child Components of this Component.
     */
    fun childComponents(): Set<Component>

    /**
     * Adds a task that is run periodically while the Window is open.
     *
     * @param runnable The task to run, may update signals
     * @param intervalInTicks The interval for the task in ticks
     * @return This builder for chaining
     */
    fun interval(intervalInTicks: Long, runnable: Runnable)

    /**
     * Gets the direct child Component, or an empty Optional when it wasn't found.
     *
     * @param id The id of the child Component.
     * @return The child Component; or empty Component.
     */
    fun getChild(id: String?): Optional<out Component?>?

    fun outlet(outletConfig: ReceiverConsumer<Outlet>)

    fun findNextOutlet() : Outlet? {
        for (childComponent in childComponents()) {
            if (childComponent is Outlet) return childComponent
            if (childComponent is ComponentGroup) {
                val outlet = childComponent.findNextOutlet()
                if (outlet != null) return outlet
            }
        }
        return null
    }

}
