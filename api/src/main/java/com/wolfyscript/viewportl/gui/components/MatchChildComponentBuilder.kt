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

import com.wolfyscript.viewportl.gui.components.MatchChildComponentBuilder.Cases
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.function.ReceiverFunction
import java.util.function.Supplier
import kotlin.reflect.KClass

interface MatchChildComponentBuilder {

    fun <V : Any> match(valueType: KClass<V>, value: Supplier<V?>, cases: ReceiverConsumer<Cases<V>>)

    fun <V : Any> match(valueType: Class<V>, value: Supplier<V?>, cases: ReceiverConsumer<Cases<V>>) {
        match(valueType.kotlin, value, cases)
    }

    interface Cases<V : Any> {

        fun case(condition: ReceiverFunction<V?, Boolean>, builderConsumer: ReceiverConsumer<ComponentGroup>)

        fun select(condition: ReceiverFunction<V?, Boolean>, builderConsumer: ReceiverConsumer<ComponentGroup>) {
            case(condition, builderConsumer)
        }

    }

    fun buildMatchers(parent: Component?)

}

inline fun <reified V : Any> MatchChildComponentBuilder.match(value: Supplier<V?>, cases: ReceiverConsumer<Cases<V>>) {
    match(V::class, value, cases)
}