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
package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.config.jackson.KeyedBaseType
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.ComponentBuilder
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import com.wolfyscript.utilities.gui.reactivity.Signal

@KeyedBaseType(baseType = ComponentBuilder::class)
abstract class AbstractComponentBuilderImpl<OWNER : Component?, PARENT : Component?> protected constructor(
    private val id: String,
    @JvmField protected val wolfyUtils: WolfyUtils,
    @field:JsonProperty("position") private var position: PropertyPosition
) : ComponentBuilder<OWNER, PARENT> {
    @JsonProperty("type")
    override val type: NamespacedKey = wolfyUtils.identifiers.getNamespaced(javaClass)

    @JsonIgnore
    private val signals: MutableSet<Signal<*>> = HashSet()

    override fun id(): String = id

    override fun position(): PropertyPosition? = position

    /**
     *
     */
    @JsonSetter("position")
    override fun position(position: PropertyPosition): ComponentBuilder<OWNER, PARENT> {
        this.position = position
        return this
    }

    protected fun addSignals(signals: Collection<Signal<*>>) {
        this.signals.addAll(signals)
    }

    override fun signals(): Set<Signal<*>> {
        return signals
    }

    override fun key(): NamespacedKey = type
}
