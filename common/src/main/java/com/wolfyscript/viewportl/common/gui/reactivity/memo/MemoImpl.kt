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

package com.wolfyscript.viewportl.common.gui.reactivity.memo

import com.wolfyscript.viewportl.common.gui.reactivity.NodeId
import com.wolfyscript.viewportl.gui.reactivity.Memo
import kotlin.reflect.KProperty

class MemoImpl<V : Any?>(val id: NodeId, private val type: Class<V>) : Memo<V> {

    override fun get(): V {
        id.runtime.reactiveSource.subscribe(id)
        return getNoTracking()
    }

    override fun getNoTracking(): V {
        return id.runtime.reactiveSource.getValue(id, type)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
//        ScafallProvider.get().logger.info("Get value for Ref: $thisRef, Property: $property")
        return get()
    }

}