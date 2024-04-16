package com.wolfyscript.utilities.gui.components

import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.animation.Animation
import com.wolfyscript.utilities.gui.animation.AnimationBuilder
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrame
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrameBuilder
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.components.ButtonBuilderImpl.IconBuilderImpl
import com.wolfyscript.utilities.gui.rendering.RenderProperties
import com.wolfyscript.utilities.world.items.ItemStackConfig
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*
import java.util.function.Supplier

@KeyedStaticId(key = "button")
class ButtonImpl : AbstractComponentImpl, Button {
    private val interactionCallback: InteractionCallback
    private val icon: ButtonIcon
    private val soundFunction: Supplier<Optional<Sound>>
    private val animation: Animation<ButtonAnimationFrame>?

    internal constructor(
        wolfyUtils: WolfyUtils?,
        id: String?,
        parent: Component?,
        icon: IconBuilderImpl,
        soundFunction: Supplier<Optional<Sound>>,
        interactionCallback: InteractionCallback,
        properties: RenderProperties?,
        animation: AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder>?
    ) : super(id!!, wolfyUtils!!, parent, properties!!) {
        this.icon = icon.create(this)
        this.interactionCallback = interactionCallback
        this.soundFunction = soundFunction
        this.animation = animation?.build(this)
    }

    private constructor(button: ButtonImpl) : super(
        button.id,
        button.wolfyUtils,
        button.parent(),
        button.properties()
    ) {
        this.interactionCallback = button.interactionCallback
        this.icon = button.icon
        this.soundFunction = button.soundFunction
        this.animation = button.animation // TODO: Properly copy
    }

    override fun icon(): ButtonIcon {
        return icon
    }

    override fun sound(): Optional<Sound> {
        return soundFunction.get()
    }

    override fun interactCallback(): InteractionCallback {
        return interactionCallback
    }

    override fun insert(viewRuntimeImpl: ViewRuntimeImpl, parentNode: Long) {
        val id = viewRuntimeImpl.renderingGraph.addNode(this)
        viewRuntimeImpl.renderingGraph.insertNodeChild(id, parentNode)
    }

    override fun remove(viewRuntimeImpl: ViewRuntimeImpl, nodeId: Long, parentNode: Long) {
        viewRuntimeImpl.renderingGraph.removeNode(nodeId)
    }

    class DynamicIcon internal constructor(private val config: ItemStackConfig, private val resolvers: TagResolver) :
        ButtonIcon {
        override fun getStack(): ItemStackConfig {
            return config
        }

        override fun getResolvers(): TagResolver {
            return resolvers
        }
    }
}
