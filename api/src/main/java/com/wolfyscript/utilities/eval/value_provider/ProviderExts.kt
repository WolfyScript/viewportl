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

package com.wolfyscript.utilities.eval.value_provider

fun String.provider() : ValueProvider<String> {
    return ValueProviderStringConst(this)
}

fun Int.provider() : ValueProvider<Int> {
    return ValueProviderIntegerConst(this)
}

fun Long.provider() : ValueProvider<Long> {
    return ValueProviderLongConst(this)
}

fun Byte.provider() : ValueProvider<Byte> {
    return ValueProviderByteConst(this)
}

fun Short.provider() : ValueProvider<Short> {
    return ValueProviderShortConst(this)
}
