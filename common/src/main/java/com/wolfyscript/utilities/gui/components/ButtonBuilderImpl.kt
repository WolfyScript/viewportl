package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.*
import com.google.common.base.Preconditions
import com.google.inject.Inject
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.animation.*
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.components.ButtonBuilder.IconBuilder
import com.wolfyscript.utilities.gui.components.ButtonImpl.DynamicIcon
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.SerializableFunction
import com.wolfyscript.utilities.gui.model.UpdateInformation
import com.wolfyscript.utilities.gui.reactivity.EffectImpl
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.gui.reactivity.SignalImpl
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import com.wolfyscript.utilities.gui.rendering.PropertyPosition.Companion.def
import com.wolfyscript.utilities.gui.rendering.RenderPropertiesImpl
import com.wolfyscript.utilities.world.items.ItemStackConfig
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

@KeyedStaticId(key = "button")
@ComponentBuilderSettings(base = ButtonBuilder::class, component = Button::class)
class ButtonBuilderImpl : AbstractComponentBuilderImpl<Button, com.wolfyscript.utilities.gui.Component>, ButtonBuilder {

    private var interactionCallback = InteractionCallback { _, _ ->
        InteractionResult.cancel(true)
    }

    private var soundFunction = Supplier {
        Optional.of(Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.MASTER, 0.25f, 1f))
    }

    private val iconBuilder: IconBuilderImpl
    private var animationBuilder: AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder>? = null
    private val context: BuildContext

    /**
     * Constructor used for non-config setups using Guice injection.
     *
     * @param id The id of the button.
     * @param wolfyUtils The wolfyutils that this button belongs to.
     */
    @Inject
    private constructor(
        id: String,
        wolfyUtils: WolfyUtils,
        buildContext: BuildContext
    ) : super(id, wolfyUtils) {
        this.context = buildContext
        this.iconBuilder = IconBuilderImpl(wolfyUtils, context)
    }

    @JsonCreator
    constructor(
        @JsonProperty("id") id: String?,
        @JsonProperty("icon") iconBuilder: IconBuilderImpl,
        @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils?,
        @JacksonInject("context") context: BuildContext
    ) : super(id!!, wolfyUtils!!) {
        this.iconBuilder = iconBuilder
        this.context = context
    }

    override fun icon(consumer: ReceiverConsumer<IconBuilder>): ButtonBuilder {
        with(consumer) { iconBuilder.consume() }
        return this
    }

    override fun interact(interactionCallback: InteractionCallback): ButtonBuilder {
        Preconditions.checkArgument(interactionCallback != null, "InteractionCallback must be non-null!")
        this.interactionCallback = interactionCallback
        return this
    }

    override fun sound(soundFunction: Supplier<Optional<Sound>>): ButtonBuilder {
        Preconditions.checkArgument(soundFunction != null, "Sound function must be non-null!")
        this.soundFunction = soundFunction
        return this
    }

    override fun animation(animationBuild: ReceiverConsumer<AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder>>): ButtonBuilder {
        val builder: AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder> =
            AnimationBuilderImpl(context) { ButtonAnimationFrameBuilderImpl(wolfyUtils) }
        with(animationBuild) { builder.consume() }
        this.animationBuilder = builder
        return this
    }

    override fun create(parent: com.wolfyscript.utilities.gui.Component?): Button {
        return ButtonImpl(
            wolfyUtils,
            id(),
            parent,
            iconBuilder,
            soundFunction,
            interactionCallback,
            position()?.let { RenderPropertiesImpl(it) } ?: RenderPropertiesImpl(def()),
            animationBuilder
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class IconBuilderImpl : IconBuilder {
        private val wolfyUtils: WolfyUtils
        private val context: BuildContext
        private var staticStackConfig: ItemStackConfig? = null
        private val itemHelper: ItemHelper
        private val tagResolvers: MutableList<TagResolver> = ArrayList()
        private val signals: MutableSet<Signal<*>> = HashSet()

        @Inject
        internal constructor(wolfyUtils: WolfyUtils, context: BuildContext) {
            // Used for non-config setups
            this.wolfyUtils = wolfyUtils
            this.itemHelper = ItemHelperImpl(wolfyUtils)
            this.context = context
        }

        /**
         * Constructor for reading the icon builder from config.
         *
         * @param staticStackConfig The necessary stack config.
         */
        @JsonCreator
        constructor(@JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils, @JacksonInject("context") context: BuildContext, @JsonProperty("stack") staticStackConfig: ItemStackConfig?) {
            this.wolfyUtils = wolfyUtils
            this.context = context
            this.staticStackConfig = staticStackConfig
            this.itemHelper = ItemHelperImpl(wolfyUtils)
        }

        @JsonSetter("stack")
        private fun setStack(config: ItemStackConfig) {
            this.staticStackConfig = config
        }

        override fun stack(itemId: String, configure: Consumer<ItemStackConfig>): IconBuilder {
            this.staticStackConfig = wolfyUtils.core.platform.items.createStackConfig(wolfyUtils, itemId)
            configure.accept(staticStackConfig!!)
            return this
        }

        override fun stack(stackConfigSupplier: SerializableFunction<ItemHelper, ItemStackConfig>): IconBuilder {
            // TODO
            return this
        }

        override fun updateOnSignals(vararg signals: Signal<*>): IconBuilder {
            tagResolvers.addAll(
                Arrays.stream(signals)
                    .map { signal: Signal<*> ->
                        signal.tagName()?.let {
                            TagResolver.resolver(it) { _, _ ->
                                Tag.inserting(
                                    Component.text(signal.get().toString())
                                )
                            }
                        }
                    }
                    .toList())
            this.signals.clear()
            this.signals.addAll(listOf(*signals))
            return this
        }

        fun addTagResolver(vararg tagResolvers: TagResolver): IconBuilder {
            this.tagResolvers.add(TagResolver.resolver(*tagResolvers))
            return this
        }

        override fun create(button: Button): ButtonIcon {
            val runtime = context.runtime as ViewRuntimeImpl
            context.reactiveSource.createEffect<Unit> {
                runtime.incomingUpdate(object : UpdateInformation {
                    override fun updated(): List<Long> = listOf(button.nodeId())
                })
            }
            return DynamicIcon(staticStackConfig ?: wolfyUtils.core.platform.items.createStackConfig(wolfyUtils, "air"), TagResolver.resolver(tagResolvers))
        }
    }
}
