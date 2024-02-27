package com.wolfyscript.utilities.gui

import com.fasterxml.jackson.annotation.*
import com.google.common.base.Preconditions
import com.google.inject.*
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.config.jackson.KeyedBaseType
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.functions.*
import com.wolfyscript.utilities.gui.signal.Signal
import com.wolfyscript.utilities.tuple.Pair
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*
import java.util.function.Consumer

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class WindowBuilderImpl @Inject @JsonCreator constructor(
    @param:JsonProperty("id") private val id: String,
    @param:JacksonInject("wolfyUtils") private val wolfyUtils: WolfyUtils,
    @param:JacksonInject("reactiveSrc") private val reactiveSource: ReactiveSource
) : WindowBuilder {
    private var size: Int = 0
    private var type: WindowType? = null
    private var interactionCallback =
        InteractionCallback { guiHolder: GuiHolder?, interactionDetails: InteractionDetails? -> InteractionResult.def() }

    /**
     * Components
     */
    private val componentIdAliases: MutableMap<String, Long> = HashMap()
    private val componentBuilderMap: MutableMap<Long, ComponentBuilder<*, *>> = HashMap()
    val componentRenderSet: MutableSet<ComponentBuilder<*, *>> = HashSet()

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

    override fun title(title: String): WindowBuilder {
        this.staticTitle = title
        return this
    }

    @JsonSetter("placement")
    private fun setPlacement(componentBuilders: List<ComponentBuilder<*, *>>) {
        for (componentBuilder in componentBuilders) {
            val numericId = ComponentUtil.nextId()
            componentIdAliases[componentBuilder.id()] = numericId
            wolfyUtils.logger.info("Load component builder from config: " + componentBuilder.id() + "  (" + numericId + ")")
            componentBuilderMap[numericId] = componentBuilder
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
                TagResolver.resolver(signal.tagName()) { argumentQueue: ArgumentQueue?, context: Context? ->
                    Tag.inserting(net.kyori.adventure.text.Component.text(signal.get().toString()))
                }
            }
                .toList())
        titleSignals.addAll(Arrays.stream(signals).toList())
        return this
    }

    override fun addIntervalTask(runnable: Runnable, l: Long): WindowBuilder {
        intervalRunnables.add(Pair(runnable, l))
        return this
    }

    override fun reactive(reactiveFunction: SignalableReceiverFunction<ReactiveRenderBuilder, ReactiveResult?>): WindowBuilder {
        val effect: Effect = object : Effect {
            private var previousComponent: Component? = null

            override fun update(guiViewManager: ViewRuntime, guiHolder: GuiHolder, context: RenderContext) {
                val builder = ReactiveRenderBuilderImpl(
                    wolfyUtils,
                    HashMap() /* TODO: ((WindowImpl) guiHolder.getCurrentWindow()).nonRenderedComponents */
                )

                val result = with(reactiveFunction) { builder.apply() }

                val component = if (result == null) null else result.construct()!!
                    .construct(guiHolder, guiViewManager)
                if (previousComponent == component) return

                if (previousComponent != null) {
                    previousComponent!!.remove(guiHolder, guiViewManager, context)
                }

                previousComponent = component
                if (component == null) {
                    return
                }

                context.enterNode(component)
                component.executeForAllSlots(
                    component.offset() + component.position().slot(),
                    Consumer { internalSlot: Int? ->
                        (guiHolder.viewManager as ViewRuntimeImpl).updateLeaveNodes(
                            component,
                            internalSlot!!
                        )
                    })
                if (component is Effect) {
                    component.update(guiViewManager, guiHolder, context)
                }
                context.exitNode()
            }
        }
        for (signal in reactiveFunction.signalsUsed) {
            signal.linkTo(effect)
        }
        return this
    }

    override fun <B : ComponentBuilder<out Component?, Component?>> conditionalComponent(
        condition: SerializableSupplier<Boolean>,
        id: String,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        val builder = findExistingComponentBuilder(id, builderTypeInfo.value, builderTypeInfo.key)
            .orElseThrow {
                IllegalStateException(
                    String.format(
                        "Failed to link to component '%s'! Cannot find existing placement",
                        id
                    )
                )
            }

        with(builderConsumer) { builder.consume() }
        val component = builder.create(null)!!

        val effect: Effect = object : Effect {
            private val previousResult: MutableMap<Long, Boolean> = HashMap()

            override fun update(guiViewManager: ViewRuntime, guiHolder: GuiHolder, context: RenderContext) {
                val result = condition.get()
                if (result != previousResult.getOrDefault(guiViewManager.id(), false)) {
                    previousResult[guiViewManager.id()] = result
                    if (result) {
                        context.enterNode(component)
                        val signalledObject = component.construct(guiHolder, guiViewManager)
                        if (signalledObject is Effect) {
                            signalledObject.update(guiViewManager, guiHolder, context)
                        }
                        component.executeForAllSlots(
                            component.offset() + component.position().slot()
                        ) { slot2: Int? ->
                            (guiHolder.viewManager as ViewRuntimeImpl).updateLeaveNodes(
                                component,
                                slot2!!
                            )
                        }
                        context.exitNode()
                    } else {
                        component.executeForAllSlots(component.offset() + component.position().slot()) { slot2: Int? ->
                            context.setStack(slot2!!, null)
                            (guiHolder.viewManager as ViewRuntimeImpl).updateLeaveNodes(null, slot2)
                        }
                    }
                }
            }
        }

        for (signal in condition.signalsUsed) {
            signal.linkTo(effect)
        }
        return this
    }

    override fun <BV : ComponentBuilder<out Component?, Component?>, BI : ComponentBuilder<out Component?, Component?>> renderWhenElse(
        serializableSupplier: SerializableSupplier<Boolean>,
        validBuilderType: Class<BV>,
        validBuilder: Consumer<BV>,
        invalidBuilderType: Class<BI>,
        invalidBuilder: SignalableReceiverConsumer<BI>
    ): WindowBuilder {
        return this
    }

    override fun <B : ComponentBuilder<out Component?, Component?>> component(
        id: String,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        val builder = findExistingComponentBuilder(id, builderTypeInfo.value, builderTypeInfo.key).orElseThrow {
            IllegalStateException(
                String.format("Failed to link to component '%s'! Cannot find existing placement", id)
            )
        }
        val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
            binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
            binder.bind(String::class.java).toInstance(id)
        })
        injector.injectMembers(builder)

        with(builderConsumer) { builder.consume() }
        componentRenderSet.add(builder!!)
        return this
    }

    override fun <B : ComponentBuilder<out Component?, Component?>> component(
        position: Position,
        id: String,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        findExistingComponentBuilder<B>(id, builderTypeInfo.value, builderTypeInfo.key).ifPresentOrElse(
            { with(builderConsumer) { it.consume() } },
            {
                val builder = instantiateNewBuilder(id, position, builderTypeInfo)
                with(builderConsumer) { builder.consume() }
                componentRenderSet.add(builder)
            })
        return this
    }

    override fun <B : ComponentBuilder<out Component?, Component?>> component(
        position: Position,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        val builder = instantiateNewBuilder(id, position, builderTypeInfo)
        with(builderConsumer) { builder.consume() }
        componentRenderSet.add(builder)
        return this
    }

    private fun <B : ComponentBuilder<out Component?, Component?>> instantiateNewBuilder(
        id: String,
        position: Position,
        builderTypeInfo: Pair<NamespacedKey, Class<B>>
    ): B {
        val numericId = ComponentUtil.nextId()
        componentIdAliases[id] = numericId

        val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
            binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
            binder.bind(Long::class.java).toInstance(numericId)
            binder.bind(Position::class.java).toInstance(position)
            binder.bind(ReactiveSource::class.java).toInstance(reactiveSource)
        })

        val builder = injector.getInstance(builderTypeInfo.value)
        componentBuilderMap[numericId] = builder!!
        return builder
    }

    private fun <B : ComponentBuilder<out Component?, Component?>> findExistingComponentBuilder(
        id: String,
        builderImplType: Class<B>,
        builderKey: NamespacedKey
    ): Optional<B> {
        if (!componentIdAliases.containsKey(id)) return Optional.empty() // If there is no alias yet, then it wasn't created yet!

        return findExistingComponentBuilder(componentIdAliases[id]!!, builderImplType, builderKey)
    }

    private fun <B : ComponentBuilder<out Component?, Component?>> findExistingComponentBuilder(
        id: Long,
        builderImplType: Class<B>,
        builderKey: NamespacedKey
    ): Optional<B> {
        val componentBuilder = componentBuilderMap[id] ?: return Optional.empty()
        if (componentBuilder.type != builderKey) {
            return Optional.empty()
        }
        return Optional.of(builderImplType.cast(componentBuilder))
    }

    override fun create(parent: Router): Window {
        if (titleFunction == null && !titleTagResolvers.isEmpty()) {
            titleFunction = SerializableSupplier {
                wolfyUtils.chat.miniMessage.deserialize(
                    staticTitle!!, TagResolver.resolver(titleTagResolvers)
                )
            }
        }
        if (titleFunction != null) {
            val signalledObject =
                Effect { viewManager: ViewRuntime?, guiHolder: GuiHolder?, renderContext: RenderContext ->
                    renderContext.updateTitle(
                        guiHolder,
                        titleFunction!!.get()
                    )
                }
            for (signal in titleFunction!!.signalsUsed) {
                signal.linkTo(signalledObject)
            }
            for (signal in titleSignals) {
                signal.linkTo(signalledObject)
            }
        }

        val components = componentRenderSet.stream()
            .map { componentBuilder -> componentBuilder.create(null) as Component }
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
}
