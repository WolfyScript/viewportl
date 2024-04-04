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

package com.wolfyscript.utilities.gui.functions

import com.wolfyscript.utilities.gui.reactivity.NodeId
import com.wolfyscript.utilities.gui.reactivity.SignalImpl
import java.io.Serializable
import java.lang.invoke.SerializedLambda

fun SignalledSerializable.getNodeIds() : Collection<NodeId> {
    return getUsedReactiveNodes(this)
}

fun getUsedReactiveNodes(serializable: Serializable): Collection<NodeId> {
    try {
        // Using serialized lambda we have access to runtime information, such as which outer variables are captured and used inside the lambda.
        // See: https://stackoverflow.com/a/35223119
        val s = getSerializedLambda(serializable)
        val signals: MutableList<NodeId> = ArrayList(s.capturedArgCount)
        for (i in 0 until s.capturedArgCount) {
            val id = when(val value = s.getCapturedArg(i)) {
                is SignalImpl<*> -> value.id()
                else -> null
            }
            if (id != null) {
                signals.add(id)
            }
        }
        return signals
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return listOf()
}

@Throws(Exception::class)
private fun getSerializedLambda(lambda: Serializable): SerializedLambda {
    val method = lambda.javaClass.getDeclaredMethod("writeReplace")
    method.isAccessible = true
    return method.invoke(lambda) as SerializedLambda
}