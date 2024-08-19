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

package com.wolfyscript.viewportl.common.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.animation.Animation
import com.wolfyscript.viewportl.gui.animation.ButtonAnimationFrame
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.components.ButtonIcon
import com.wolfyscript.viewportl.gui.components.Component
import com.wolfyscript.viewportl.gui.components.ComponentImplementation
import com.wolfyscript.viewportl.gui.model.UpdateInformation
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.function.Supplier
import javax.annotation.Nullable

@ComponentImplementation(base = Button::class)
@StaticNamespacedKey(key = "button")
class ButtonImpl @JsonCreator @Inject constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("viewportl") viewportl: Viewportl,
    @JacksonInject("context") private val context: BuildContext,
    @Nullable @JacksonInject("parent") parent: Component? = null,
) : AbstractComponentImpl<Button>(id, viewportl, parent), Button {

    private val animation: Animation<ButtonAnimationFrame>? = null

    override var icon: ButtonIcon = DynamicIcon(viewportl, context, this)
    override var onClick: ReceiverConsumer<ClickTransaction>? = null

    override fun icon(iconConsumer: ReceiverConsumer<ButtonIcon>) {
        with(iconConsumer) {
            icon.consume()
        }
    }

    override var sound: Sound? = Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.MASTER, 0.25f, 1f)

    override fun insert(runtime: ViewRuntime, parentNode: Long) {
        runtime as ViewRuntimeImpl
        val id = runtime.modelGraph.addNode(this)
        runtime.modelGraph.insertNodeAsChildOf(id, parentNode)
    }

    override fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long) {
        (runtime as ViewRuntimeImpl).modelGraph.removeNode(nodeId)
    }

    override fun completeBuild() {
        icon.completeBuild()
    }

    class DynamicIcon internal constructor(
        @JacksonInject("viewportl") private val viewportl: Viewportl,
        @JacksonInject("context") private val context: BuildContext,
        @JacksonInject("button") private val button: Button,
    ) : ButtonIcon {

        override var stack: ItemStackConfig = context.runtime.viewportl.scafall.factories.itemsFactory.createStackConfig(com.wolfyscript.scafall.identifier.Key.key(com.wolfyscript.scafall.identifier.Key.MINECRAFT_NAMESPACE, "air"))
        override var resolvers: TagResolver = TagResolver.empty()

        override fun stack(stackSupplier: Supplier<ItemStackConfig>) {
            stack = stackSupplier.get()
        }

        override fun stack(itemId: String, stackConfig: ReceiverConsumer<ItemStackConfig>) {
            context.reactiveSource.createEffect {
                val newStack = context.runtime.viewportl.scafall.factories.itemsFactory.createStackConfig(com.wolfyscript.scafall.identifier.Key.key(com.wolfyscript.scafall.identifier.Key.MINECRAFT_NAMESPACE, itemId))
                with(stackConfig) { newStack.consume() }
                stack = newStack

                (context.runtime as ViewRuntimeImpl).incomingUpdate(object : UpdateInformation {
                    override fun updated(): List<Long> = listOf(button.nodeId())
                })
            }
        }

        override fun resolvers(resolverSupplier: Supplier<TagResolver>) {
            context.reactiveSource.createEffect {
                resolvers = resolverSupplier.get()

                (context.runtime as ViewRuntimeImpl).incomingUpdate(object : UpdateInformation {
                    override fun updated(): List<Long> = listOf(button.nodeId())
                })
            }
        }
    }
}
