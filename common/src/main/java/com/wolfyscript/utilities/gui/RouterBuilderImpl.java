package com.wolfyscript.utilities.gui;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RouterBuilderImpl implements RouterBuilder {

    private final String route;
    private final WolfyUtils wolfyUtils;
    private final ReactiveSource reactiveSource;
    private final Map<String, RouterBuilder> subRouteBuilders = new HashMap<>();
    private WindowBuilder windowBuilder = null;
    private InteractionCallback interactionCallback = (guiHolder, interactionDetails) -> InteractionResult.def();

    @JsonCreator
    RouterBuilderImpl(@JsonProperty("route") String route,
                      @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils,
                      @JacksonInject("reactiveSrc") ReactiveSource reactiveSource) {
        this.route = route;
        this.wolfyUtils = wolfyUtils;
        this.reactiveSource = reactiveSource;
    }

    @JsonSetter("window")
    private void readWindow(WindowBuilderImpl windowBuilder) {
        this.windowBuilder = windowBuilder;
    }

    @JsonSetter("routes")
    private void readRoutes(List<RouterBuilderImpl> routes) {
        for (RouterBuilderImpl subRouter : routes) {
            subRouteBuilders.put(subRouter.route, subRouter);
        }
    }

    @Override
    public RouterBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkArgument(interactionCallback != null);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public RouterBuilder route(String s, BiConsumer<ReactiveSource, RouterBuilder> consumer) {
        consumer.accept(reactiveSource, subRouteBuilders.computeIfAbsent(s, s1 -> new RouterBuilderImpl(s, wolfyUtils, reactiveSource)));
        return this;
    }

    @Override
    public RouterBuilder window(BiConsumer<ReactiveSource, WindowBuilder> consumer) {
        consumer.accept(reactiveSource, window());
        return this;
    }

    @Override
    public WindowBuilder window() {
        if (this.windowBuilder == null) {
            this.windowBuilder = new WindowBuilderImpl("", wolfyUtils, reactiveSource);
        }
        return windowBuilder;
    }

    public Router create(Router parent) {
        return new RouterImpl(
                route,
                wolfyUtils,
                windowBuilder,
                parent,
                interactionCallback
        );
    }
}
