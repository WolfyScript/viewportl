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

package com.wolfyscript.viewportl.gui.router

import java.util.*
import kotlin.reflect.KClass

class ActivePath(private val path: MutableList<Section> = mutableListOf()) : Iterable<ActivePath.Section> {

    private val params: MutableMap<String, Param<*>> = LinkedHashMap()

    val size: Int get() = path.size
    val pathSections: List<Section> get() = Collections.unmodifiableList(path.toList())

    fun getParam(key: String): Param<*>? {
        return params[key]
    }

    fun push(section: Section) {
        addSection(section)
    }

    fun pop() : Section = removeSection()

    fun peek() : Section = path.last()

    operator fun div(section: String) : ActivePath {
        addSection(StaticSection(section))
        return this
    }

    operator fun div(param: Param<*>) : ActivePath {
        addSection(param)
        return this
    }

    operator fun times(param: Class<*>) : ActivePath {
        return times(param.kotlin)
    }

    operator fun times(param: KClass<*>) : ActivePath {
        val last = path.last()
        if (last is StaticSection) {
            addSection(Param(last.name, param.java))
        }
        return this
    }

    private fun removeSection() : Section {
        val section = path.removeLast()
        params.remove(section.name)
        return section
    }

    private fun addSection(section: Section) {
        if (section is Param<*>) {
            params[section.name] = section
        }
        path.add(section)
    }

    fun copy() : ActivePath {
        val newPath = ActivePath(path.map { it.copy() }.toMutableList())
        return newPath
    }

    override fun toString(): String {
        return path.joinToString(separator = "/", prefix = "/")
    }

    interface Section {

        val name: String

        fun copy() : Section

    }

    class StaticSection(override val name: String) : Section {
        override fun copy(): Section {
            return this
        }

        override fun toString(): String {
            return name
        }
    }

    class Param<T: Any>(override val name: String, val value: T) : Section {
        override fun copy(): Section {
            return this
        }

        override fun toString(): String {
            return "<$name> ($value)"
        }
    }

    override fun iterator(): Iterator<Section> {
        return Collections.unmodifiableList(path).iterator()
    }

}