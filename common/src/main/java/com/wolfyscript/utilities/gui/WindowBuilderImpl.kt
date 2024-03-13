package com.wolfyscript.utilities.gui

import com.fasterxml.jackson.annotation.*
import com.google.common.base.Preconditions
import com.google.inject.Inject
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.config.jackson.KeyedBaseType
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.components.ComponentUtil
import com.wolfyscript.utilities.gui.components.ConditionalChildComponentBuilder
import com.wolfyscript.utilities.gui.components.ConditionalChildComponentBuilderImpl
import com.wolfyscript.utilities.gui.functions.*
import com.wolfyscript.utilities.gui.reactivity.AnyComputation
import com.wolfyscript.utilities.gui.reactivity.EffectImpl
import com.wolfyscript.utilities.gui.reactivity.ReactiveSource
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.gui.rendering.RenderingNode
import com.wolfyscript.utilities.tuple.Pair
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*
import java.util.function.Consumer

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class WindowBuilderImpl @Inject @JsonCreator constructor(
    @JsonProperty("id") private val id: String,
    @JacksonInject("wolfyUtils") private val wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext
) : WindowBuilder, ReactiveSource by context.reactiveSource {

    private var size: Int = 0
    private var type: WindowType? = null
    private var interactionCallback = InteractionCallback { _, _ -> InteractionResult.def() }

    /**
     * Components
     */
    private val componentRenderSet: MutableSet<Long> = HashSet()
    private val conditionals: MutableList<ConditionalChildComponentBuilderImpl<WindowBuilder>> = mutableListOf()

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
                TagResolver.resolver(signal.tagName()) { _, _ ->
                    Tag.inserting(net.kyori.adventure.text.Component.text(signal.get().toString()))
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

    override fun reactive(reactiveFunction: SignalableReceiverFunction<ReactiveRenderBuilder, ReactiveResult?>): WindowBuilder {
        val builder = ReactiveRenderBuilderImpl(wolfyUtils, context)
        val component = with(reactiveFunction) { builder.apply() }?.construct()

        context.reactiveSource.createCustomEffect(emptyList(), null, object : AnyComputation<Long?> {

            override fun run(
                runtime: ViewRuntime,
                value: Long?,
                apply: Consumer<Long?>
            ): Boolean {
                runtime as ViewRuntimeImpl
                val graph = runtime.renderingGraph
                val previousNode: RenderingNode? = value?.let { graph.getNode(it) }
                if (previousNode?.component == component) return false

                val previousComponent = previousNode?.component
                if (previousComponent is Renderable) {
                    previousComponent.remove(runtime, previousNode.id, 0)
                }

                if (component == null) {
                    apply.accept(null)
                    return true
                }

                val id = runtime.renderingGraph.addNode(component)
                apply.accept(id)

                return true
            }
        })
        return this
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String?,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): WindowBuilder {
        val numericId = context.getOrCreateNumericId(id)
        val builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id ?: "internal_${id}", builderType)
        val builder: B = context.findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
                val builderId = context.instantiateNewBuilder(numericId, Position(Position.Type.RELATIVE, 0) /* TODO */, builderTypeInfo)
                componentRenderSet.add(builderId)
                context.getBuilder(builderId, builderTypeInfo.value)
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

        conditionals.forEach { it.build(null) }

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
            val effect = context.reactiveSource.createEffect() {
                window.title(titleFunction!!.get())
            }
            val effectNode = context.reactiveSource.untypedNode((effect as EffectImpl).id)
            titleFunction!!.getNodeIds().forEach {
                val node = context.reactiveSource.untypedNode(it)
                if (node != null) {
                    effectNode?.subscribe(node)
                }
            }
        }

        return window
    }

    override fun whenever(condition: SerializableSupplier<Boolean>): ConditionalChildComponentBuilder.When<WindowBuilder> {
        val builder: ConditionalChildComponentBuilderImpl<WindowBuilder> = ConditionalChildComponentBuilderImpl(this, context)
        conditionals.add(builder)
        return builder.whenever(condition)
    }
}
