package com.wolfyscript.utilities.gui;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.config.jackson.KeyedBaseType;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.functions.SerializableConsumer;
import com.wolfyscript.utilities.gui.functions.SerializableFunction;
import com.wolfyscript.utilities.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.tuple.Pair;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.*;
import java.util.function.Consumer;

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindowBuilderImpl implements WindowBuilder {

    private final String id;
    private final WolfyUtils wolfyUtils;
    protected int size;
    protected WindowType type;
    private final ReactiveSource reactiveSource;
    private InteractionCallback interactionCallback = (guiHolder, interactionDetails) -> InteractionResult.def();

    /**
     * Components
     */
    private final Map<String, Long> componentIdAliases = new HashMap<>();
    private final Map<Long, ComponentBuilder<?, ?>> componentBuilderMap = new HashMap<>();
    final Set<ComponentBuilder<?, ?>> componentRenderSet = new HashSet<>();

    /**
     * Tasks
     */
    protected List<Pair<Runnable, Long>> intervalRunnables = new ArrayList<>();

    /**
     * Title data
     */
    private final List<TagResolver> titleTagResolvers = new ArrayList<>();
    private String staticTitle = null;
    private SerializableSupplier<net.kyori.adventure.text.Component> titleFunction;
    private final Set<com.wolfyscript.utilities.gui.signal.Signal<?>> titleSignals = new HashSet<>();

    @Inject
    @JsonCreator
    protected WindowBuilderImpl(@JsonProperty("id") String windowID,
                                @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils,
                                @JacksonInject("reactiveSrc") ReactiveSource reactiveSource) {
        this.id = windowID;
        this.wolfyUtils = wolfyUtils;
        this.reactiveSource = reactiveSource;
    }

    @JsonSetter("size")
    private void setSize(int size) {
        this.size = size;
    }

    @Override
    public WindowBuilder size(int size) {
        this.size = size;
        return this;
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.staticTitle = title;
    }

    @JsonGetter("title")
    public String getStaticTitle() {
        return staticTitle;
    }

    @JsonSetter("inventory_type")
    @Override
    public WindowBuilder type(WindowType type) {
        this.type = type;
        return this;
    }

    @Override
    public WindowBuilder title(String title) {
        this.staticTitle = title;
        return this;
    }

    @JsonSetter("placement")
    private void setPlacement(List<ComponentBuilder<?, ?>> componentBuilders) {
        for (ComponentBuilder<?, ?> componentBuilder : componentBuilders) {
            long numericId = ComponentUtil.nextId();
            componentIdAliases.put(componentBuilder.id(), numericId);
            wolfyUtils.getLogger().info("Load component builder from config: " + componentBuilder.id() + "  (" + numericId + ")");
            componentBuilderMap.put(numericId, componentBuilder);
        }
    }

    @Override
    public WindowBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(interactionCallback);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public WindowBuilder title(SerializableSupplier<net.kyori.adventure.text.Component> titleSupplier) {
        this.titleFunction = titleSupplier;
        return this;
    }

    @Override
    public WindowBuilder titleSignals(Signal<?>... signals) {
        titleTagResolvers.addAll(Arrays.stream(signals).map(signal -> TagResolver.resolver(signal.tagName(), (argumentQueue, context) -> Tag.inserting(net.kyori.adventure.text.Component.text(String.valueOf(signal.get()))))).toList());
        titleSignals.addAll(Arrays.stream(signals).toList());
        return this;
    }

    @Override
    public WindowBuilder addIntervalTask(Runnable runnable, long l) {
        this.intervalRunnables.add(new Pair<>(runnable, l));
        return this;
    }

    @Override
    public WindowBuilder reactive(SerializableFunction<ReactiveRenderBuilder, ReactiveRenderBuilder.ReactiveResult> consumer) {
        Effect effect = new Effect() {

            private Component previousComponent = null;

            @Override
            public void update(ViewRuntime guiViewManager, GuiHolder guiHolder, RenderContext context) {
                ReactiveRenderBuilderImpl builder = new ReactiveRenderBuilderImpl(wolfyUtils, new HashMap<>() /* TODO: ((WindowImpl) guiHolder.getCurrentWindow()).nonRenderedComponents */);
                ReactiveRenderBuilder.ReactiveResult result = consumer.apply(builder);
                Component component = result == null ? null : result.construct().construct(guiHolder, guiViewManager);
                if (Objects.equals(previousComponent, component)) return;

                if (previousComponent != null) {
                    previousComponent.remove(guiHolder, guiViewManager, context);
                }

                previousComponent = component;
                if (component == null) {
                    return;
                }

                context.enterNode(component);
                component.executeForAllSlots(component.offset() + component.position().slot(), internalSlot -> ((ViewRuntimeImpl) guiHolder.getViewManager()).updateLeaveNodes(component, internalSlot));
                if (component instanceof Effect signalledObject) {
                    signalledObject.update(guiViewManager, guiHolder, context);
                }
                context.exitNode();
            }
        };
        for (com.wolfyscript.utilities.gui.signal.Signal<?> signal : consumer.getSignalsUsed()) {
            signal.linkTo(effect);
        }
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder conditionalComponent(SerializableSupplier<Boolean> condition, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id, builderType);
        B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
        builderConsumer.accept(builder);

        Component component = builder.create(null);

        Effect effect = new Effect() {

            private final Map<Long, Boolean> previousResult = new HashMap<>();

            @Override
            public void update(ViewRuntime guiViewManager, GuiHolder guiHolder, RenderContext context) {
                boolean result = condition.get();
                if (result != previousResult.getOrDefault(guiViewManager.id(), false)) {
                    previousResult.put(guiViewManager.id(), result);
                    if (result) {
                        context.enterNode(component);
                        if (component.construct(guiHolder, guiViewManager) instanceof Effect signalledObject) {
                            signalledObject.update(guiViewManager, guiHolder, context);
                        }
                        component.executeForAllSlots(component.offset() + component.position().slot(), slot2 -> ((ViewRuntimeImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot2));
                        context.exitNode();
                    } else {
                        component.executeForAllSlots(component.offset() + component.position().slot(), slot2 -> {
                            context.setStack(slot2, null);
                            ((ViewRuntimeImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot2);
                        });
                    }
                }
            }
        };

        for (com.wolfyscript.utilities.gui.signal.Signal<?> signal : condition.getSignalsUsed()) {
            signal.linkTo(effect);
        }
        return this;
    }

    @Override
    public <BV extends ComponentBuilder<? extends Component, Component>, BI extends ComponentBuilder<? extends Component, Component>> WindowBuilder renderWhenElse(SerializableSupplier<Boolean> serializableSupplier, Class<BV> validBuilderType, Consumer<BV> validBuilder, Class<BI> invalidBuilderType, SerializableConsumer<BI> invalidBuilder) {
        return null;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder component(String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id, builderType);
        B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey()).orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
            binder.bind(String.class).toInstance(id);
        });
        injector.injectMembers(builder);
        builderConsumer.accept(builder);
        componentRenderSet.add(builder);
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder component(Position position, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id, builderType);
        findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey()).ifPresentOrElse(builderConsumer, () -> {
            B builder = instantiateNewBuilder(id, position, builderTypeInfo);
            builderConsumer.accept(builder);
            componentRenderSet.add(builder);
        });
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder component(Position position, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id, builderType);
        B builder = instantiateNewBuilder(id, position, builderTypeInfo);
        builderConsumer.accept(builder);
        componentRenderSet.add(builder);
        return this;
    }

    private <B extends ComponentBuilder<? extends Component, Component>> B instantiateNewBuilder(String id, Position position, Pair<NamespacedKey, Class<B>> builderTypeInfo) {
        long numericId = ComponentUtil.nextId();
        componentIdAliases.put(id, numericId);

        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
            binder.bind(Long.class).toInstance(numericId);
            binder.bind(Position.class).toInstance(position);
            binder.bind(ReactiveSource.class).toInstance(reactiveSource);
        });

        B builder = injector.getInstance(builderTypeInfo.getValue());
        componentBuilderMap.put(numericId, builder);
        return builder;
    }

    private <B extends ComponentBuilder<? extends Component, Component>> Optional<B> findExistingComponentBuilder(String id, Class<B> builderImplType, NamespacedKey builderKey) {
        if (!componentIdAliases.containsKey(id)) return Optional.empty(); // If there is no alias yet, then it wasn't created yet!
        return findExistingComponentBuilder(componentIdAliases.get(id), builderImplType, builderKey);
    }

    private <B extends ComponentBuilder<? extends Component, Component>> Optional<B> findExistingComponentBuilder(long id, Class<B> builderImplType, NamespacedKey builderKey) {
        ComponentBuilder<?, ?> componentBuilder = componentBuilderMap.get(id);
        if (componentBuilder == null) {
            return Optional.empty();
        }
        if (!componentBuilder.getType().equals(builderKey)) {
            return Optional.empty();
        }
        return Optional.of(builderImplType.cast(componentBuilder));
    }

    @Override
    public Window create(Router parent) {

        if (titleFunction == null && !titleTagResolvers.isEmpty()) {
            titleFunction = () -> wolfyUtils.getChat().getMiniMessage().deserialize(staticTitle, TagResolver.resolver(titleTagResolvers));
        }
        if (titleFunction != null) {
            Effect signalledObject = (viewManager, guiHolder, renderContext) -> renderContext.updateTitle(guiHolder, titleFunction.get());
            for (Signal<?> signal : titleFunction.getSignalsUsed()) {
                signal.linkTo(signalledObject);
            }
            for (Signal<?> signal : titleSignals) {
                signal.linkTo(signalledObject);
            }
        }

        var components = componentRenderSet.stream().map(componentBuilder -> (Component) componentBuilder.create(null)).toList();

        return new WindowImpl(
                parent.getID() + "/" + id,
                parent,
                size,
                type,
                staticTitle,
                titleFunction,
                interactionCallback,
                components
        );
    }

}
