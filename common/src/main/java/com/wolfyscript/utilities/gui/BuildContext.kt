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
import com.google.inject.Provider
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

    private val componentIdAliases: MutableMap<String, Long> = HashMap()
    private val componentMap: MutableMap<Long, Component> = HashMap()

    fun <B : Component> getOrCreateComponent(
        alias: String? = null,
        type: Class<B>,
        getId: Consumer<Long> = Consumer{ }
    ): B {
        val id = getOrCreateNumericId(alias)
        val (builderKey, implType) = getBuilderType(alias ?: "internal_${id}", type)

        val component = componentMap[id]
        if (component != null) {
            if (component.type() != builderKey) {
                throw IllegalArgumentException("Incompatible Component Builder Type! Expected type '$builderKey' but existing builder is of type '${component.type()}'!")
            }
        }
        val builder = implType.cast(component) ?: run {
            val builderId = instantiateNewBuilder(id, Pair(builderKey, implType))
            getBuilder(builderId, implType) ?: throw IllegalStateException("Created builder $builderId of type '${type}' (impl: ${implType}), but it still wasn't available!")
        }
        getId.accept(id) // We only want to run this when no errors came before
        return builder
    }

    private fun <B : Component> instantiateNewBuilder(numericId: Long, builderTypeInfo: Pair<NamespacedKey, Class<B>>): Long {
        val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
            binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
            binder.bind(Long::class.java).toInstance(numericId)
            binder.bind(BuildContext::class.java).toInstance(this)
            binder.bind(Component::class.java).toProvider(Providers.of(null))
        })

        val builder = injector.getInstance(builderTypeInfo.value)
        componentMap[numericId] = builder!!
        return numericId
    }

    private fun getOrCreateNumericId(namedId: String? = null): Long {
        if (namedId != null) {
            if (componentIdAliases.containsKey(namedId)) return componentIdAliases[namedId]!!
            val numericId = nextId()
            componentIdAliases[namedId] = numericId
            return numericId
        }
        return nextId()
    }

    private fun <B : Component> getBuilderType(
        id: String?,
        builderType: Class<B>
    ): kotlin.Pair<NamespacedKey, Class<B>> {
        val registry = runtime.wolfyUtils.registries.guiComponentTypes
        val key = registry.getKey(builderType)
        Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", id, builderType.name)
        val builderImplType = registry[key] as Class<B> // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
        Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", id, builderType.name)
        return kotlin.Pair(key, builderImplType)
    }

    fun registerComponent(component: Component) : Long {
        val numericId = nextId()
        component.id?.let {
            componentIdAliases[it] = numericId
        }
        wolfyUtils.logger.fine("Register Component Builder: " + component.id + "  (" + numericId + ")")
        componentMap[numericId] = component
        return numericId
    }

    fun getBuilder(id: Long) : Component? {
        return componentMap[id]
    }

    fun <B> getBuilder(id: Long, builderType: Class<B>) : B? {
        val untyped = getBuilder(id) ?: return null
        if (!builderType.isInstance(untyped)) throw IllegalArgumentException("Builder with $id is not of type ${builderType.name}")
        return builderType.cast(untyped)
    }

}
