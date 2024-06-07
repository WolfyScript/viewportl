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

package com.wolfyscript.utilities.gui

import com.google.common.base.Preconditions
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.google.inject.util.Providers
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.components.Component
import com.wolfyscript.utilities.gui.reactivity.ReactiveSourceImpl
import com.wolfyscript.utilities.tuple.Pair
import java.util.function.Consumer

class BuildContext(val runtime: ViewRuntime, val reactiveSource: ReactiveSourceImpl, val wolfyUtils: WolfyUtils) {

    private companion object {
        private var COMPONENT_COUNTER: Long = 0

        fun nextId(): Long {
            return COMPONENT_COUNTER++
        }
    }

    private val componentIdAliases: MutableMap<String, Component> = HashMap()

    fun <B : Component> getOrCreateComponent(
        parent: Component? = null,
        alias: String? = null,
        type: Class<B>,
        getId: Consumer<Long> = Consumer{ }
    ): B {
        val id = getOrCreateNumericId(alias)
        val (builderKey, implType) = getComponentType(alias ?: "internal_${id}", type)
        val component = instantiateNewComponent(parent, id, Pair(builderKey, implType))
        getId.accept(id) // We only want to run this when no errors came before
        if (alias != null) {
            componentIdAliases[alias] = component
        }
        return component
    }

    private fun <B : Component> instantiateNewComponent(parent: Component? = null, numericId: Long, builderTypeInfo: Pair<NamespacedKey, Class<B>>): B {
        val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
            binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
            binder.bind(Long::class.java).toInstance(numericId)
            binder.bind(BuildContext::class.java).toInstance(this)
            binder.bind(Component::class.java).toProvider(Providers.of(parent))
        })
        return injector.getInstance(builderTypeInfo.value)
    }

    private fun getOrCreateNumericId(namedId: String? = null): Long {
        if (namedId != null && componentIdAliases.containsKey(namedId)) {
            return componentIdAliases[namedId]!!.nodeId()
        }
        return nextId()
    }

    private fun <B : Component> getComponentType(
        id: String?,
        type: Class<B>
    ): kotlin.Pair<NamespacedKey, Class<B>> {
        val registry = runtime.wolfyUtils.registries.guiComponentTypes
        val key = registry.getKey(type)
        Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", id, type.name)
        val builderImplType = registry[key] as Class<B> // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
        Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", id, type.name)
        return kotlin.Pair(key, builderImplType)
    }

}
