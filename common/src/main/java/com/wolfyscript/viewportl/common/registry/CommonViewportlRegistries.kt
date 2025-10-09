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

package com.wolfyscript.viewportl.common.registry

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference
import com.wolfyscript.scafall.registry.RegistrySimple
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.elements.ButtonImpl
import com.wolfyscript.viewportl.common.gui.elements.GroupImpl
import com.wolfyscript.viewportl.common.gui.elements.SlotImpl
import com.wolfyscript.viewportl.registry.ViewportlRegistries
import com.wolfyscript.viewportl.registry.ViewportlRegistryTypes

class CommonViewportlRegistries(val viewportl: Viewportl) : ViewportlRegistries {

    private val rootRegistry = RegistrySimple<Registry<*>>(ViewportlRegistryTypes.root)

    fun initRegistries() {
        createRegistry(ViewportlRegistryTypes.guiElementTypes) {
            RegistryUIComponentImplementations(it).apply {
                register(Key.key("viewportl", "button"), ButtonImpl::class.java)
                register(Key.key("viewportl", "slot"), SlotImpl::class.java)
                register(Key.key("viewportl", "group"), GroupImpl::class.java)
            }
        }
    }

    fun <T> createRegistry(type: RegistryReference<T>, loader: (key: Key) -> Registry<T>) {
        val registry = loader(type.key.registry)
        rootRegistry.register(type.key.registry, registry)
    }

    override fun <T> get(type: RegistryKey<T>): Result<Registry<T>> {
        val registry = rootRegistry[type.registry]
            ?: return Result.failure(IllegalArgumentException("No registry found for ${type.registry}"))
        return Result.success(registry as Registry<T>)
    }
}