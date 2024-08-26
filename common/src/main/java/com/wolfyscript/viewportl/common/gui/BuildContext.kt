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

package com.wolfyscript.viewportl.common.gui

import com.google.common.base.Preconditions
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.google.inject.util.Providers
import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.reactivity.ReactiveGraph
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.Component
import java.util.function.Consumer

class BuildContext(val runtime: ViewRuntime, val reactiveSource: ReactiveGraph, val viewportl: Viewportl) {

    private companion object {
        private var COMPONENT_COUNTER: Long = 0

        fun nextId(): Long {
            return COMPONENT_COUNTER++
        }
    }

    // TODO: Do not store Components here! They are handled by the ModelGraph! When removed from the graph the alias is no longer valid!
    private val componentIdAliases: MutableMap<String, Component> = mutableMapOf()

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

    private fun <B : Component> instantiateNewComponent(parent: Component? = null, numericId: Long, builderTypeInfo: Pair<Key, Class<B>>): B {
        val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
            binder.bind(Viewportl::class.java).toInstance(viewportl)
            binder.bind(Long::class.java).toInstance(numericId)
            binder.bind(BuildContext::class.java).toInstance(this)
            binder.bind(Component::class.java).toProvider(Providers.of(parent))
        })
        return injector.getInstance(builderTypeInfo.second)
    }

    private fun getOrCreateNumericId(namedId: String? = null): Long {
        if (namedId != null && componentIdAliases.containsKey(namedId)) {
            return componentIdAliases[namedId]?.nodeId() ?: nextId()
        }
        return nextId()
    }

    private fun <B : Component> getComponentType(
        id: String?,
        type: Class<B>
    ): Pair<Key, Class<B>> {
        val registry = runtime.viewportl.registries.guiComponents
//        val registry = runtime.scaffolding.registries.getByKeyOfType(Key.key(Key.SCAFFOLDING_NAMESPACE, "component/types"), RegistryGUIComponentTypes::class.java)
        val key = registry.getKey(type) ?: throw IllegalArgumentException("Could not find component of type $type")
        val builderImplType = registry[key] as Class<B> // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
        Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", id, type.name)
        return Pair(key, builderImplType)
    }

}
