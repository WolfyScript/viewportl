package com.wolfyscript.utilities.gui

import com.fasterxml.jackson.annotation.*
import com.google.common.base.Preconditions
import com.google.inject.*
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.config.jackson.KeyedBaseType
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.components.ConditionalChildComponentBuilder
import com.wolfyscript.utilities.gui.functions.*
import com.wolfyscript.utilities.gui.reactivity.AnyComputation
import com.wolfyscript.utilities.gui.reactivity.EffectImpl
import com.wolfyscript.utilities.gui.signal.Signal
import com.wolfyscript.utilities.tuple.Pair
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*
import java.util.function.Consumer

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class WindowBuilderImpl @Inject @JsonCreator constructor(
    @param:JsonProperty("id") private val id: String,
    @param:JacksonInject("wolfyUtils") private val wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext
) : WindowBuilder {
    private var size: Int = 0
    private var type: WindowType? = null
    private var interactionCallback =
        InteractionCallback { guiHolder: GuiHolder?, interactionDetails: InteractionDetails? -> InteractionResult.def() }

    /**
     * Components
     */
    private val componentRenderSet: MutableSet<Long> = HashSet()

    /**
     * Tasks
     */
    protected var intervalRunnables: MutableList<Pair<Runnable, Long>> = ArrayList()

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
            }.toList())
        titleSignals.addAll(Arrays.stream(signals).toList())
        return this
    }

    override fun addIntervalTask(runnable: Runnable, intervalInTicks: Long): WindowBuilder {
        intervalRunnables.add(Pair(runnable, intervalInTicks))
        return this
    }

    override fun reactive(reactiveFunction: SignalableReceiverFunction<ReactiveRenderBuilder, ReactiveResult?>): WindowBuilder {
        context.reactiveSource.createCustomEffect(emptyList(), null, object : AnyComputation<Component> {

            override fun run(
                runtime: ViewRuntime,
                holder: GuiHolder,
                value: Component?,
                apply: Consumer<Component?>
            ): Boolean {
                val previousComponent: Component? = value;

                val builder = ReactiveRenderBuilderImpl(
                    wolfyUtils,
                    HashMap() /* TODO: ((WindowImpl) guiHolder.getCurrentWindow()).nonRenderedComponents */
                )

                val result = with(reactiveFunction) { builder.apply() }

                val component = if (result == null) null else result.construct()!!
                if (value == component) return true

                (runtime as ViewRuntimeImpl).getRenderContext(holder.player.uuid()).ifPresent {

                    previousComponent?.remove(holder, runtime, it)

                    apply.accept(component)
                    if (component == null) {
                        return@ifPresent
                    }

                    it.enterNode(component)
                    component.executeForAllSlots(
                        component.offset() + component.position().slot(),
                        Consumer { internalSlot: Int? ->
                            runtime.updateLeaveNodes(
                                component,
                                internalSlot!!
                            )
                        })
                    component.render(runtime, holder, it)
                    it.exitNode()
                }

                return true
            }
        })
        return this
    }

    fun <B : ComponentBuilder<out Component, Component>> conditionalComponent(
        condition: SerializableSupplier<Boolean>,
        id: String,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        val builder = context.findExistingComponentBuilder(0, builderTypeInfo.value, builderTypeInfo.key)
            .orElseThrow {
                IllegalStateException(
                    String.format(
                        "Failed to link to component '%s'! Cannot find existing placement",
                        id
                    )
                )
            }

        with(builderConsumer) { builder.consume() }
        val component = builder.create(null)

        val effect = context.reactiveSource.createCustomEffect(emptyList(), false, object : AnyComputation<Boolean> {

            override fun run(
                runtime: ViewRuntime,
                holder: GuiHolder,
                value: Boolean?,
                apply: Consumer<Boolean?>
            ): Boolean {
                runtime as ViewRuntimeImpl
                val result = condition.get()
                if (result != value) {
                    apply.accept(result)
                    runtime.getRenderContext(holder.player.uuid()).ifPresent { context ->
                        if (result) {
                            context.enterNode(component)
                            component.render(runtime, holder, context)
                            component.executeForAllSlots(
                                component.offset() + component.position().slot()
                            ) { slot2: Int? ->
                                runtime.updateLeaveNodes(
                                    component,
                                    slot2!!
                                )
                            }
                            context.exitNode()
                        } else {
                            component.executeForAllSlots(
                                component.offset() + component.position().slot()
                            ) { slot2: Int? ->
                                context.setStack(slot2!!, null)
                                runtime.updateLeaveNodes(null, slot2)
                            }
                        }
                    }
                }

                return true
            }
        })

        val effectNode = context.reactiveSource.untypedNode((effect as EffectImpl).id)
        condition.getNodeIds().forEach {
            val node = context.reactiveSource.untypedNode(it)
            if (node != null) {
                effectNode?.subscribe(node)
            }
        }
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
        if (titleFunction != null) {
            val effect = context.reactiveSource.createCustomEffect(titleSignals.toList(), null, object : AnyComputation<Any> {

                override fun run(runtime: ViewRuntime, holder: GuiHolder, value: Any?, apply: Consumer<Any?>): Boolean {
                    (runtime as ViewRuntimeImpl).getRenderContext(holder.player.uuid()).ifPresent {
                        it.updateTitle(
                            holder,
                            titleFunction!!.get()
                        )
                    }
                    return true
                }
            })
            val effectNode = context.reactiveSource.untypedNode((effect as EffectImpl).id)
            titleFunction!!.getNodeIds().forEach {
                val node = context.reactiveSource.untypedNode(it)
                if (node != null) {
                    effectNode?.subscribe(node)
                }
            }
        }

        val components = componentRenderSet.stream()
            .map { componentBuilder -> context.getBuilder(componentBuilder)?.create(null) as Component }
            .toList()

        return WindowImpl(
            parent.id + "/" + id,
            parent,
            size,
            type,
            staticTitle,
            titleFunction,
            interactionCallback,
            components
        )
    }

    override fun whenever(condition: SerializableSupplier<Boolean>): ConditionalChildComponentBuilder.When<WindowBuilder> {
        TODO("Not yet implemented")
    }
}
