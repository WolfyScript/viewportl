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

package com.wolfyscript.viewportl.common.gui.elements

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.elements.ElementGroup
import com.wolfyscript.viewportl.gui.elements.ElementImplementation
import com.wolfyscript.viewportl.gui.elements.GroupProperties
import java.util.*
import kotlin.math.abs

internal fun setupGroup(properties: GroupProperties) {
    val runtime = properties.scope.runtime.into()

    val group = GroupImpl("", runtime.viewportl, properties.scope.parent?.component)
    // Add the button once on init
    val id = (properties.scope as ComponentScopeImpl).setComponent(group)
    properties.content(properties.scope)
}

@ElementImplementation(base = ElementGroup::class)
@StaticNamespacedKey(key = "cluster")
class GroupImpl @JsonCreator @Inject constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("viewportl") viewportl: Viewportl,
    @javax.annotation.Nullable @JacksonInject("parent") parent: Element? = null,
) : AbstractElementImpl<ElementGroup>(id, viewportl, parent), ElementGroup {

    private val children: MutableList<Element> = mutableListOf()
    private val width: Int
    private val height: Int

    init {
        val topLeft = 54
        this.width = 1
        this.height = (abs((topLeft / 9).toDouble()) + 1).toInt()
    }

    override fun childComponents(): Set<Element> {
        return HashSet(children)
    }

    override fun getChild(id: String?): Optional<out Element> {
        return Optional.empty()
    }

}


