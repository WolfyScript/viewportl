package com.wolfyscript.utilities.gui

import com.fasterxml.jackson.annotation.*
import com.google.common.base.Preconditions
import com.google.inject.Inject
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.config.jackson.KeyedBaseType
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.components.ConditionalChildComponentBuilder
import com.wolfyscript.utilities.gui.components.ConditionalChildComponentBuilderImpl
import com.wolfyscript.utilities.gui.components.MatchChildComponentBuilder
import com.wolfyscript.utilities.gui.components.MatchChildComponentBuilderImpl
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.SerializableSupplier
import com.wolfyscript.utilities.gui.model.UpdateInformation
import com.wolfyscript.utilities.gui.reactivity.ReactiveSource
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.tuple.Pair
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class WindowBuilderImpl @Inject @JsonCreator constructor(
    @JsonProperty("id") private val id: String,
    @JacksonInject("wolfyUtils") private val wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext
) : WindowBuilder,
    ReactiveSource by context.reactiveSource,
    ConditionalChildComponentBuilder by ConditionalChildComponentBuilderImpl(context),
    MatchChildComponentBuilder by MatchChildComponentBuilderImpl(context) {

    private var size: Int = 0
    private var type: WindowType? = null
    private var interactionCallback = InteractionCallback { _, _ -> InteractionResult.def() }

    /**
     * Components
     */
    private val componentRenderSet: MutableSet<Long> = HashSet()

    /**
     * Tasks
     */
    private var intervalRunnables: MutableList<Pair<Runnable, Long>> = ArrayList()

    /**
     * Title data
     */
    private val titleTagResolvers: MutableList<TagResolver> = ArrayList()

    @get:JsonGetter("title")
    var staticTitle: String? = null
        private set
    private var titleFunction: SerializableSupplier<net.kyori.adventure.text.Component>? = null
    private val titleSignals: MutableSet<Signal<*>> = HashSet()

    @JsonSetter("size")
    private fun setSize(size: Int) {
        this.size = size
    }

    override fun size(size: Int): WindowBuilder {
        this.size = size
        return this
    }

    @JsonSetter("title")
    fun setTitle(title: String?) {
        this.staticTitle = title
    }

    @JsonSetter("inventory_type")
    override fun type(type: WindowType): WindowBuilder {
        this.type = type
        return this
    }

    override fun title(staticTitle: String): WindowBuilder {
        this.staticTitle = staticTitle
        return this
    }

    @JsonSetter("placement")
    private fun setPlacement(componentBuilders: List<ComponentBuilder<*, *>>) {
        for (componentBuilder in componentBuilders) {
            context.registerBuilder(componentBuilder)
        }
    }

    override fun interact(interactionCallback: InteractionCallback): WindowBuilder {
        Preconditions.checkNotNull(interactionCallback)
        this.interactionCallback = interactionCallback
        return this
    }

    override fun title(titleSupplier: SerializableSupplier<net.kyori.adventure.text.Component>): WindowBuilder {
        this.titleFunction = titleSupplier
        return this
    }

    override fun titleSignals(vararg signals: Signal<*>): WindowBuilder {
        titleTagResolvers.addAll(
            Arrays.stream(signals).map { signal: Signal<*> ->
                signal.tagName()?.let {
                    TagResolver.resolver(it) { _, _ ->
                        Tag.inserting(net.kyori.adventure.text.Component.text(signal.get().toString()))
                    }
                }
            }.toList()
        )
        titleSignals.addAll(Arrays.stream(signals).toList())
        return this
    }

    override fun addIntervalTask(runnable: Runnable, intervalInTicks: Long): WindowBuilder {
        intervalRunnables.add(Pair(runnable, intervalInTicks))
        return this
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String?,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): WindowBuilder {
        val builder = context.getOrCreateComponentBuilder(id, builderType) {
            if (!componentRenderSet.contains(it)) {
                componentRenderSet.add(it)
            }
        }
        with(builderConsumer) { builder.consume() }
        return this
    }

    override fun create(parent: Router): Window {
        if (titleFunction == null && titleTagResolvers.isNotEmpty()) {
            titleFunction = SerializableSupplier {
                wolfyUtils.chat.miniMessage.deserialize(
                    staticTitle!!, TagResolver.resolver(titleTagResolvers)
                )
            }
        }

        buildConditionals(null)
        buildMatchers(null)

        val components = componentRenderSet.stream()
            .map { componentBuilder -> context.getBuilder(componentBuilder)?.create(null) as Component }
            .toList()

        val window = WindowImpl(
            parent.id + "/" + id,
            parent,
            size,
            type,
            titleFunction?.get() ?: net.kyori.adventure.text.Component.empty(),
            interactionCallback,
            components
        )

        if (titleFunction != null) {
            val runtime = context.runtime as ViewRuntimeImpl
            context.reactiveSource.createEffect<Unit> {
                window.title(titleFunction!!.get())
                runtime.incomingUpdate(object : UpdateInformation {
                    override fun updateTitle(): Boolean = true
                })
            }
        }

        return window
    }
}
