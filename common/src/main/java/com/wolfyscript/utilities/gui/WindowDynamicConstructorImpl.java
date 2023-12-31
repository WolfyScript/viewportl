package com.wolfyscript.utilities.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.functions.SerializableConsumer;
import com.wolfyscript.utilities.gui.functions.SerializableFunction;
import com.wolfyscript.utilities.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.gui.signal.Store;
import com.wolfyscript.utilities.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.tuple.Pair;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WindowDynamicConstructorImpl implements WindowDynamicConstructor {

    final WolfyUtils wolfyUtils;
    final GuiViewManager viewManager;
    final GuiHolder holder;
    final WindowImpl window;
    final Map<ComponentBuilder<?, ?>, Position> componentBuilderPositions = new HashMap<>();
    final Set<ComponentBuilder<?, ?>> componentRenderSet = new HashSet<>();
    final Map<String, Signal<?>> usedSignals = new HashMap<>();
    private final List<TagResolver> titleTagResolvers = new ArrayList<>();
    private SerializableSupplier<net.kyori.adventure.text.Component> titleFunction;
    private final Set<Signal<?>> titleSignals = new HashSet<>();

    protected List<Pair<Runnable, Long>> intervalRunnables = new ArrayList<>();

    public WindowDynamicConstructorImpl(@JacksonInject WolfyUtils wolfyUtils, GuiHolder holder, WindowImpl window) {
        this.wolfyUtils = wolfyUtils;
        this.window = window;
        this.holder = holder;
        this.viewManager = holder.getViewManager();

        this.componentBuilderPositions.putAll(window.nonRenderedComponents);
    }

    @Override
    public WindowDynamicConstructorImpl title(SerializableSupplier<net.kyori.adventure.text.Component> titleSupplier) {
        this.titleFunction = titleSupplier;
        return this;
    }

    @Override
    public WindowDynamicConstructorImpl titleSignals(Signal<?>... signals) {
        titleTagResolvers.addAll(Arrays.stream(signals)
                .map(signal -> TagResolver.resolver(signal.key(), (argumentQueue, context) -> Tag.inserting(net.kyori.adventure.text.Component.text(String.valueOf(signal.get())))))
                .toList());
        titleSignals.addAll(Arrays.stream(signals).toList());
        return this;
    }

    @Override
    public GuiViewManager viewManager() {
        return viewManager;
    }

    @Override
    public GuiHolder holder() {
        return holder;
    }

    @Override
    public <T> Signal<T> signal(String s, Class<T> aClass, Supplier<T> defaultValueFunction) {
        if (usedSignals.containsKey(s)) {
            Signal<?> usedSignal = usedSignals.get(s);
            Preconditions.checkState(usedSignal.valueType().equals(aClass), String.format("Failed to use existing state '%s'! Incompatible types: expected '%s' but got '%s'", s, usedSignal.valueType(), aClass));
            return (Signal<T>) usedSignal;
        }
        var signal = new SignalImpl<>(s, viewManager, aClass, defaultValueFunction);
        usedSignals.put(s, signal);
        return signal;
    }

    @Override
    public <T> Store<T> syncStore(String s, Class<T> aClass, Supplier<T> supplier, Consumer<T> consumer) {
        if (usedSignals.containsKey(s)) {
            Signal<?> usedSignal = usedSignals.get(s);
            Preconditions.checkState(usedSignal.valueType().equals(aClass), String.format("Failed to use existing store '%s'! Incompatible types: expected '%s' but got '%s'", s, usedSignal.valueType(), aClass));
            Preconditions.checkState(usedSignal instanceof Store<?>, String.format("Failed to use existing signal '%s' as store!", s));
            return (Store<T>) usedSignal;
        }
        var store = new StoreImpl<>(s, viewManager, aClass, supplier, consumer);
        usedSignals.put(s, store);
        return store;
    }

    @Override
    public WindowDynamicConstructorImpl addIntervalTask(Runnable runnable, long l) {
        this.intervalRunnables.add(new Pair<>(runnable, l));
        return this;
    }

    @Override
    public WindowDynamicConstructorImpl reactive(SerializableFunction<ReactiveRenderBuilder, ReactiveRenderBuilder.ReactiveResult> consumer) {
        SignalledObject signalledObject = new SignalledObject() {

            private Component previousComponent = null;

            @Override
            public void update(GuiViewManager guiViewManager, GuiHolder guiHolder, RenderContext context) {
                ReactiveRenderBuilderImpl builder = new ReactiveRenderBuilderImpl(wolfyUtils, window.nonRenderedComponents);
                ReactiveRenderBuilder.ReactiveResult result = consumer.apply(builder);
                Component component = result == null ? null : result.construct().construct(guiHolder, viewManager);
                if (Objects.equals(previousComponent, component)) return;

                if (previousComponent != null) {
                    previousComponent.remove(guiHolder, guiViewManager, context);
                }

                previousComponent = component;
                if (component == null) {
                    return;
                }

                context.enterNode(component);
                component.executeForAllSlots(component.offset() + component.position().slot(), internalSlot -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, internalSlot));
                if (component instanceof SignalledObject signalledObject) {
                    signalledObject.update(viewManager, guiHolder, context);
                }
                context.exitNode();
            }
        };
        for (Signal<?> signal : consumer.getSignalsUsed()) {
            signal.linkTo(signalledObject);
        }
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructorImpl renderWhen(SerializableSupplier<Boolean> condition, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
        B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
        builderConsumer.accept(builder);

        Component component = builder.create(null);

        SignalledObject signalledObject = new SignalledObject() {

            private boolean previousResult = false;

            @Override
            public void update(GuiViewManager guiViewManager, GuiHolder guiHolder, RenderContext context) {
                boolean result = condition.get();
                if (result != previousResult) {
                    previousResult = result;
                    if (result) {
                        context.enterNode(component);
                        if (component.construct(guiHolder, viewManager) instanceof SignalledObject signalledObject) {
                            signalledObject.update(viewManager, guiHolder, context);
                        }
                        component.executeForAllSlots(component.offset() + component.position().slot(), slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot2));
                        context.exitNode();
                    } else {
                        component.executeForAllSlots(component.offset() + component.position().slot(), slot2 -> {
                            context.setStack(slot2, null);
                            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot2);
                        });
                    }
                }
            }
        };

        for (Signal<?> signal : condition.getSignalsUsed()) {
            signal.linkTo(signalledObject);
        }
        return this;
    }

    @Override
    public <BV extends ComponentBuilder<? extends Component, Component>, BI extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructorImpl renderWhenElse(SerializableSupplier<Boolean> serializableSupplier, Class<BV> validBuilderType, Consumer<BV> validBuilder, Class<BI> invalidBuilderType, SerializableConsumer<BI> invalidBuilder) {
        return null;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructorImpl render(String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
        B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
        builderConsumer.accept(builder);
        componentRenderSet.add(builder);
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructorImpl renderAt(Position position, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
        findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey()).ifPresentOrElse(builderConsumer, () -> {
            B builder = instantiateNewBuilder(id, builderTypeInfo);
            builderConsumer.accept(builder);
            componentBuilderPositions.put(builder, position);
            componentRenderSet.add(builder);
        });
        return this;
    }

    private <B extends ComponentBuilder<? extends Component, Component>> B instantiateNewBuilder(String id, Pair<NamespacedKey, Class<B>> builderTypeInfo) {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
            binder.bind(String.class).toInstance(id);
            binder.bind(DynamicConstructor.class).toInstance(this);
        });
        return injector.getInstance(builderTypeInfo.getValue());
    }

    private <B extends ComponentBuilder<? extends Component, Component>> Optional<B> findExistingComponentBuilder(String id, Class<B> builderImplType, NamespacedKey builderKey) {
        return componentBuilderPositions.keySet().stream()
                .filter(componentBuilder -> componentBuilder.id().equals(id) && componentBuilder.getType().equals(builderKey))
                .findFirst()
                .map(builderImplType::cast);
    }

    static <B extends ComponentBuilder<? extends Component, Component>> Pair<NamespacedKey, Class<B>> getBuilderType(WolfyUtils wolfyUtils, String id, Class<B> builderType) {
        RegistryGUIComponentBuilders registry = wolfyUtils.getRegistries().getGuiComponentBuilders();
        NamespacedKey key = registry.getKey(builderType);
        Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", id, builderType.getName());
        @SuppressWarnings("unchecked")
        Class<B> builderImplType = (Class<B>) registry.get(key); // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
        Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", id, builderType.getName());
        return new Pair<>(key, builderImplType);
    }

    public Window create(Window staticWindow) {
        Map<Component, Position> finalPostions = new HashMap<>();
        Map<ComponentBuilder<?, ?>, Position> nonRenderedComponents = new HashMap<>();

        for (var componentBuilder : componentBuilderPositions.keySet()) {
            if (componentRenderSet.contains(componentBuilder)) {
                Component component = componentBuilder.create(null);
                finalPostions.put(component, componentBuilder.position());
                continue;
            }
            nonRenderedComponents.put(componentBuilder, componentBuilder.position());
        }

        if (titleFunction == null && !titleTagResolvers.isEmpty()) {
            titleFunction = () -> wolfyUtils.getChat().getMiniMessage().deserialize(((WindowImpl) staticWindow).getStaticTitle(), TagResolver.resolver(titleTagResolvers));
        }
        if (titleFunction != null) {
            SignalledObject signalledObject = (viewManager, guiHolder, renderContext) -> renderContext.updateTitle(guiHolder, titleFunction.get());
            for (Signal<?> signal : titleFunction.getSignalsUsed()) {
                signal.linkTo(signalledObject);
            }
            for (Signal<?> signal : titleSignals) {
                signal.linkTo(signalledObject);
            }
        }

        return ((WindowImpl) staticWindow).dynamicCopy(finalPostions, nonRenderedComponents, titleFunction, intervalRunnables);
    }

}
