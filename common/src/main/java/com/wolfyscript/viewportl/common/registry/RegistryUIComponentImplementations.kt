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

import com.google.common.base.Preconditions
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registries
import com.wolfyscript.scafall.registry.UniqueTypeRegistrySimple
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.elements.ElementImplementation
import com.wolfyscript.viewportl.registry.RegistryGUIComponentTypes

class RegistryUIComponentImplementations(key: Key, registries: Registries) : UniqueTypeRegistrySimple<Element>(key, registries),
    RegistryGUIComponentTypes {

    private val baseTypeToImpl: MutableMap<Class<out Element>, Key> = HashMap()

    override fun register(key: Key, value: Class<out Element>) {
        val settings = value.getAnnotation(ElementImplementation::class.java)
        Preconditions.checkNotNull(settings, "Missing ComponentImplementation annotation for '%s'! Requires: %s", key, ElementImplementation::class.java)
        val baseType: Class<out Element> = settings.base.java
        Preconditions.checkState(
            baseTypeToImpl[baseType] == null,
            "Failed to register Component! There can only be one implementation for a single Component Type: '%s' already has an implementation '%s'!",
            baseType.name,
            baseTypeToImpl[baseType]
        )
        super.register(key, value)
        baseTypeToImpl[baseType] = key
    }

    override fun getKey(value: Class<out Element>): Key? {
        val implKey: Key = super<UniqueTypeRegistrySimple>.getKey(value) ?: return baseTypeToImpl[value]
        return implKey
    }

    override fun getImplementation(baseElementType: Class<out Element>): Class<out Element>? {
        Preconditions.checkNotNull(baseElementType, "Cannot get Builder for null component type!")
        val key = baseTypeToImpl[baseElementType] ?: return null
        return get(key)
    }

}
