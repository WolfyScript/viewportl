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

import com.wolfyscript.utilities.gui.functions.ReceiverFunction

/**
 * A DataComponentMap contains the data applied to an ItemStack.<br></br>
 * Each data is associated with a unique key, and can be fetched and replaced.<br></br>
 *
 *
 * Each data entry is immutable! When changes are required the data needs to be copied, mutated, and reapplied.<br></br>
 * By default this will try to convert the calls to the platform specific Item Stack APIs.
 * Which means, when using it on an earlier versions it will use NBT instead.
 *
 *
 * This API is required in 1.20.5+ due to the introduction of data components that replace NBT.
 */
interface DataComponentMap {

    /**
     * Gets the data from the map associated with the specified key.
     *
     * @return The data associated with the key; null otherwise.
     */
    fun <T> get(key: ReceiverFunction<Keys, DataKey<T>>): T?

    fun <T> getOrDefault(key: ReceiverFunction<Keys, DataKey<T>>, def: T): T {
        val value: T? = this.get(key)
        return value ?: def
    }

    fun <T> set(key: ReceiverFunction<Keys, DataKey<T>>, data: T)

    fun remove(key: ReceiverFunction<Keys, DataKey<*>>) : Boolean

    fun has(key: ReceiverFunction<Keys, DataKey<*>>): Boolean

    fun keySet(): Set<DataKey<*>>

    fun size(): Int
}
