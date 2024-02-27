package com.wolfyscript.utilities.gui.components;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.gui.ComponentBuilder;
import com.wolfyscript.utilities.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.gui.Position;
import com.wolfyscript.utilities.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

@KeyedStaticId(key = "cluster")
@ComponentBuilderSettings(base = ComponentClusterBuilder.class, component = ComponentCluster.class)
public class ComponentClusterBuilderImpl extends AbstractComponentBuilderImpl<ComponentCluster, Component> implements ComponentClusterBuilder {

    private final Map<ComponentBuilder<?, Component>, Position> componentBuilderPositions = new HashMap<>();
    private final Set<ComponentBuilder<?, Component>> componentRenderSet = new HashSet<>();

    @JsonCreator
    public ComponentClusterBuilderImpl(@JsonProperty("id") String id, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JsonProperty("position") Position position) {
        super(id, wolfyUtils, position);
    }

    @JsonSetter("placement")
    private void setPlacement(List<ComponentBuilder<?, Component>> componentBuilders) {
        for (ComponentBuilder<?, Component> componentBuilder : componentBuilders) {
            componentBuilderPositions.put(componentBuilder, componentBuilder.position());
        }
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ComponentClusterBuilder render(String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(getWolfyUtils(), id, builderType);
        B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
        builderConsumer.accept(builder);
        componentRenderSet.add(builder);
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ComponentClusterBuilder renderAt(Position position, String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(getWolfyUtils(), id, builderType);
        findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey()).ifPresentOrElse(builderConsumer, () -> {
            Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                binder.bind(WolfyUtils.class).toInstance(getWolfyUtils());
                binder.bind(String.class).toInstance(id);
            });
            B builder = injector.getInstance(builderTypeInfo.getValue());
            builderConsumer.accept(builder);
            componentBuilderPositions.put(builder, position);
            componentRenderSet.add(builder);
        });
        return this;
    }

    private static <B extends ComponentBuilder<? extends Component, Component>> Pair<NamespacedKey, Class<B>> getBuilderType(WolfyUtils wolfyUtils, String id, Class<B> builderType) {
        RegistryGUIComponentBuilders registry = wolfyUtils.getRegistries().getGuiComponentBuilders();
        NamespacedKey key = registry.getKey(builderType);
        Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", id, builderType.getName());
        @SuppressWarnings("unchecked")
        Class<B> builderImplType = (Class<B>) registry.get(key); // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
        Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", id, builderType.getName());
        return new Pair<>(key, builderImplType);
    }

    private <B extends ComponentBuilder<? extends Component, Component>> Optional<B> findExistingComponentBuilder(String id, Class<B> builderImplType, NamespacedKey builderKey) {
        return componentBuilderPositions.keySet().stream()
                .filter(componentBuilder -> componentBuilder.id().equals(id) && componentBuilder.getType().equals(builderKey))
                .findFirst()
                .map(builderImplType::cast);
    }

    @Override
    public ComponentClusterImpl create(Component component) {
        List<Component> staticComponents = new ArrayList<>();
        Map<ComponentBuilder<?, ?>, Position> nonRenderedComponents = new HashMap<>();

        var build = new ComponentClusterImpl(id(), getWolfyUtils(), component, position(), staticComponents);
        for (ComponentBuilder<?, Component> componentBuilder : componentBuilderPositions.keySet()) {
            Position position = componentBuilderPositions.get(componentBuilder);

            if (componentRenderSet.contains(componentBuilder)) {
                staticComponents.add(componentBuilder.create(build));
                continue;
            }
            nonRenderedComponents.put(componentBuilder, position);
        }
        return build;
    }
}
