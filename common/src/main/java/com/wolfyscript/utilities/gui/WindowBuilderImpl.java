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
import com.wolfyscript.utilities.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.tuple.Pair;

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
    private String staticTitle = null;
    private InteractionCallback interactionCallback = (guiHolder, interactionDetails) -> InteractionResult.def();
    private Consumer<WindowDynamicConstructor> rendererConstructor = builder -> {};
    private final Map<ComponentBuilder<?, ?>, Position> componentBuilderPositions = new HashMap<>();

    @Inject
    @JsonCreator
    protected WindowBuilderImpl(@JsonProperty("id") String windowID,
                                @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        this.id = windowID;
        this.wolfyUtils = wolfyUtils;
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
            componentBuilderPositions.put(componentBuilder, componentBuilder.position());
        }
    }

    @Override
    public WindowBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(interactionCallback);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public WindowBuilder construct(Consumer<WindowDynamicConstructor> render) {
        Preconditions.checkNotNull(render);
        this.rendererConstructor = render;
        return this;
    }

    @Override
    public Window create(Router parent) {
        return new WindowImpl(
                parent.getID() + "/" + id,
                parent,
                size,
                type,
                staticTitle,
                interactionCallback,
                componentBuilderPositions,
                rendererConstructor
        );
    }

}
