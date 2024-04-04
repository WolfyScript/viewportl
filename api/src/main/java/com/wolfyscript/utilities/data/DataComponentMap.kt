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
 * Each data entry is immutable! When changes are required the data needs to be copied, mutated, and reapplied.<br>
 * By default this will try to convert the calls to the platform specific ItemStack APIs.
 *
 * In 1.20.5+ this makes use of the data components as much as the platform allows,
 * when using it on prior versions it will use the platform specific legacy implementations (e.g. ItemMeta on Spigot) instead.
 *
 */
interface DataComponentMap<H : DataHolder<H>> {

    /**
     * Gets the data from the map associated with the specified key.
     *
     * @return The data associated with the key; null otherwise.
     */
    fun <T : Any> get(key: ReceiverFunction<Keys, DataKey<T,H>>): T?

    fun <T : Any> getOrDefault(key: ReceiverFunction<Keys, DataKey<T,H>>, def: T): T {
        val value: T? = this.get(key)
        return value ?: def
    }

    fun <T : Any> set(key: ReceiverFunction<Keys, DataKey<T, H>>, data: T)

    fun remove(key: ReceiverFunction<Keys, DataKey<*, H>>) : Boolean

    fun has(key: ReceiverFunction<Keys, DataKey<*, H>>): Boolean

    fun keySet(): Set<DataKey<*, H>>

    fun size(): Int
}
