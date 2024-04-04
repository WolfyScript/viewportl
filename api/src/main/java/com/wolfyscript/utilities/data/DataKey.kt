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
package com.wolfyscript.utilities.data

import com.wolfyscript.utilities.Keyed
import com.wolfyscript.utilities.gui.functions.ReceiverBiFunction
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import kotlin.reflect.KClass

interface DataKey<T: Any, V : DataHolder<V>> : Keyed {

    fun readFrom(source: V) : T?

    fun writeTo(value: T, target: V)

    interface Builder<T : Any, V: DataHolder<V>> {

        val valueType: KClass<T>

        fun reader(readerFn: ReceiverFunction<V, T?>) : Builder<T, V>

        fun writer(writerFn: ReceiverBiFunction<V, T, V>) : Builder<T, V>

        fun build() : DataKey<T, V>

    }
}
