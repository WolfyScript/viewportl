package com.wolfyscript.utilities.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReactiveRenderBuilderImpl implements ReactiveRenderBuilder {

    final WolfyUtils wolfyUtils;
    final Map<ComponentBuilder<?, ?>, Position> componentBuilderPositions = new HashMap<>();

    public ReactiveRenderBuilderImpl(WolfyUtils wolfyUtils, Map<ComponentBuilder<?, ?>, Position> nonRenderedComponents) {
        this.wolfyUtils = wolfyUtils;
        this.componentBuilderPositions.putAll(nonRenderedComponents);
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ReactiveResult renderAt(Position position, String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = WindowDynamicConstructorImpl.getBuilderType(wolfyUtils, id, builderType);
        B builder = componentBuilderPositions.keySet().stream()
                .filter(entry -> entry.id().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                .findFirst()
                .map(builderTypeInfo.getValue()::cast)
                .orElseGet(() -> {
                    Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                        binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                        binder.bind(String.class).toInstance(id);
                    });
                    return injector.getInstance(builderTypeInfo.getValue());
                });
        componentBuilderPositions.put(builder, position);
        builderConsumer.accept(builder);

        return new ReactiveResultImpl(builder);
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ReactiveResult render(String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = WindowDynamicConstructorImpl.getBuilderType(wolfyUtils, id, builderType);
        B builder = builderTypeInfo.getValue().cast(componentBuilderPositions.keySet().stream()
                .filter(entry -> entry.id().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id))));
        builderConsumer.accept(builder);

        return new ReactiveResultImpl(builder);
    }

    public static class ReactiveResultImpl implements ReactiveResult {

        private final ComponentBuilder<?,?> builder;

        ReactiveResultImpl(ComponentBuilder<?,?> builder) {
            this.builder = builder;
        }

        @Override
        public Component construct() {
            return builder.create(null);
        }
    }

}
