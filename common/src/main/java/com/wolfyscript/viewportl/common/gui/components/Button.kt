package com.wolfyscript.viewportl.common.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.components.ButtonIcon
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.components.NativeComponentImplementation
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.function.Supplier
import javax.annotation.Nullable

@NativeComponentImplementation(base = Button::class)
@StaticNamespacedKey(key = "button")
class ButtonImpl @JsonCreator @Inject constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("viewportl") viewportl: Viewportl,
    @JacksonInject("context") private val context: BuildContext,
    @Nullable @JacksonInject("parent") parent: NativeComponent? = null,
    override var icon: ButtonIcon = DynamicIcon(context),
    override var onClick: ReceiverConsumer<ClickTransaction>? = null,
    override var sound: Sound? = Sound.sound(Key.parse("minecraft:ui.button.click").into(), Sound.Source.MASTER, 0.25f, 1f)
) : AbstractNativeComponentImpl<Button>(id, viewportl, parent), Button {

    override fun insert(runtime: ViewRuntime, parentNode: Long) {
        runtime as ViewRuntimeImpl
        val id = runtime.modelGraph.addNode(this)
        runtime.modelGraph.insertNodeAsChildOf(id, parentNode)
    }

    override fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long) {
        (runtime as ViewRuntimeImpl).modelGraph.removeNode(nodeId)
    }

}

class DynamicIcon internal constructor(
    @JacksonInject("context") private val context: BuildContext,
) : ButtonIcon {

    override var stack: ItemStackConfig = context.runtime.viewportl.scafall.factories.itemsFactory.createStackConfig(
        Key.key(Key.MINECRAFT_NAMESPACE, "air"))
    override var resolvers: TagResolver = TagResolver.empty()

    override fun stack(itemId: String, stackConfig: ReceiverConsumer<ItemStackConfig>) {
        context.reactiveSource.createEffect {
            val newStack = context.runtime.viewportl.scafall.factories.itemsFactory.createStackConfig(Key.key(Key.MINECRAFT_NAMESPACE, itemId))
            with(stackConfig) { newStack.consume() }
            stack = newStack
        }
    }

    override fun resolvers(resolverSupplier: Supplier<TagResolver>) {
        context.reactiveSource.createEffect {
            resolvers = resolverSupplier.get()
        }
    }
}
