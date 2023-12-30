package com.wolfyscript.utilities.gui;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RouterBuilderImpl implements RouterBuilder {

    private final String route;
    private final WolfyUtils wolfyUtils;
    private final Map<String, RouterBuilder> subRouteBuilders = new HashMap<>();
    private WindowBuilder windowBuilder = null;
    private InteractionCallback interactionCallback = (guiHolder, interactionDetails) -> InteractionResult.def();

    @JsonCreator
    RouterBuilderImpl(@JsonProperty("route") String route,
                      @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        this.route = route;
        this.wolfyUtils = wolfyUtils;
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
    public RouterBuilder route(String s, Consumer<RouterBuilder> consumer) {
        consumer.accept(subRouteBuilders.computeIfAbsent(s, s1 -> new RouterBuilderImpl(s, wolfyUtils)));
        return this;
    }

    @Override
    public RouterBuilder window(Consumer<WindowBuilder> consumer) {
        if (this.windowBuilder == null) {
            this.windowBuilder = new WindowBuilderImpl("", wolfyUtils);
        }
        consumer.accept(windowBuilder);
        return this;
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
