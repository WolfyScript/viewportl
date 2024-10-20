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

import java.util.Collections
import kotlin.reflect.KClass

class MatchPath(private val path: MutableList<SectionMatcher> = mutableListOf()) {

    val length: Int get() = path.size
    val sections: List<SectionMatcher> get() = Collections.unmodifiableList(path)

    operator fun div(section: String) : MatchPath {
        path.add(StaticMatcher(section))
        return this
    }

    operator fun div(param: ParamMatcher<*>) : MatchPath {
        path.add(param)
        return this
    }

    operator fun times(param: Class<*>) : MatchPath {
        return times(param.kotlin)
    }

    operator fun times(param: KClass<*>) : MatchPath {
        val last = path.last()
        if (last is StaticMatcher) {
            path.add(ParamMatcher(last.name, param.java))
        }
        return this
    }

    fun matches(activePath: ActivePath, startIndex: Int = 0) : Boolean {
        if (activePath.size > path.size) {
            return false
        }
        val activePathSections = activePath.pathSections
        for ((index, matcher) in path.withIndex()) {
            if (activePathSections.size <= startIndex + index) {
                return false
            }
            val section = activePathSections[startIndex + index]
            if (!matcher.matches(section)) {
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        return path.joinToString(separator = "/", prefix = " /")
    }

    fun copy(): MatchPath {
        return MatchPath(path.toMutableList())
    }

    data class ParamMatcher<T: Any>(
        override val name: String,
        val type: Class<T>
    ) : SectionMatcher {
        override fun matches(section: ActivePath.Section) : Boolean {
            if (section !is ActivePath.Param<*>) return false
            if (type != section.value.javaClass) return false
            return true
        }

        override fun toString(): String {
            return "<$name : $type>"
        }
    }

    data class StaticMatcher(
        override val name: String
    ) : SectionMatcher {
        override fun matches(section: ActivePath.Section) : Boolean {
            return section is ActivePath.StaticSection && section.name == name
        }

        override fun toString(): String {
            return name
        }
    }

    interface SectionMatcher {

        val name: String

        fun matches(section: ActivePath.Section) : Boolean

    }

}