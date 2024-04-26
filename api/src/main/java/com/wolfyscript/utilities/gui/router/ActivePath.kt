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

package com.wolfyscript.utilities.gui.router

import java.util.*

class ActivePath : Iterable<ActivePath.Section> {

    private val path: MutableList<Section> = mutableListOf()

    fun push(section: Section) {
        path.add(section)
    }

    fun pop() : Section = path.removeLast()

    fun peek() : Section = path.last()


    interface Section

    class StaticSection(val name: String) : Section

    class Param<T: Any>(val name: String, val value: T) : Section

    override fun iterator(): Iterator<Section> {
        return Collections.unmodifiableList(path).iterator()
    }

}