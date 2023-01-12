/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.common.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class ClusterCommonImpl<D extends Data> implements Cluster<D> {

    private final WolfyUtils wolfyUtils;
    private final Cluster<D> parent;
    private final ComponentState<D>[] states;
    private final StateSelector<D> stateSelector;
    private final String id;
    private final MenuComponent<D> entry;
    private final Class<D> dataType;
    private final BiMap<String, ? extends MenuComponent<D>> children;

    protected ClusterCommonImpl(String id, Class<D> dataType, WolfyUtils wolfyUtils, Cluster<D> parent, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends MenuComponent<D>> children, MenuComponent<D> entry) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(stateSelector);
        Preconditions.checkNotNull(dataType);
        Preconditions.checkNotNull(wolfyUtils);
        Preconditions.checkNotNull(entry, "Cluster must have an entry Component!");
        Preconditions.checkArgument(states != null && states.length > 0, "ComponentStates cannot be null or empty!");
        this.parent = parent;
        this.dataType = dataType;
        this.wolfyUtils = wolfyUtils;
        this.id = id;
        this.stateSelector = stateSelector;
        this.states = states;
        this.children = HashBiMap.create(children);
        this.entry = entry;
    }

    protected ClusterCommonImpl(String id, Cluster<D> parent, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends MenuComponent<D>> children, MenuComponent<D> entry) {
        this(id, parent.dataType(), parent.getWolfyUtils(), parent, stateSelector, states, children, entry);
    }

    protected ClusterCommonImpl(String id, WolfyUtils wolfyUtils, Class<D> dataType, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends MenuComponent<D>> children, MenuComponent<D> entry) {
        this(id, dataType, wolfyUtils, null, stateSelector, states, children, entry);
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Set<? extends MenuComponent<D>> children() {
        return children.values();
    }

    @Override
    public Class<D> dataType() {
        return dataType;
    }

    @Override
    public Cluster<D> parent() {
        return parent;
    }

    @Override
    public void open(GuiViewManager<D> viewManager, UUID player) {
        // Redirect and open the entry component
        entry().open(viewManager, player);
    }

    @Override
    public Optional<Component<D>> getChild(String id) {
        return Optional.ofNullable(children.get(id));
    }

    @Override
    public ComponentState<D> getState(GuiHolder<D> holder) {
        return states[stateSelector.run(holder, holder.getViewManager().getData(), this)];
    }

    @Override
    public void init() {

    }

    @Override
    public MenuComponent<D> entry() {
        return entry;
    }

    public static abstract class Builder<D extends Data> implements ClusterComponentBuilder<D> {

        private final String subID;
        private String entryID;
        private final WolfyUtils wolfyUtils;
        private final Cluster<D> parent;
        private final Class<D> dataType;
        private final List<ComponentState.Builder<D, ComponentState<D>>> stateBuilders = new ArrayList<>();
        private final ClusterChildComponentBuilder<D> childComponentBuilder;
        private StateSelector<D> stateSelector;

        protected Builder(String subID, Cluster<D> parent, ClusterChildComponentBuilder<D> childComponentBuilder) {
            Preconditions.checkNotNull(subID);
            this.wolfyUtils = parent.getWolfyUtils();
            this.parent = parent;
            this.dataType = parent.dataType();
            this.subID = subID;
            this.childComponentBuilder = childComponentBuilder;
        }

        @Override
        public final ClusterComponentBuilder<D> stateSelector(StateSelector<D> stateSelector) {
            this.stateSelector = stateSelector;
            return this;
        }

        public final ClusterComponentBuilder<D> state(Consumer<ComponentState.Builder<D, ComponentState<D>>> stateBuilderConsumer) {
            ComponentState.Builder<D, ComponentState<D>> stateBuilder = new ComponentStateDefault.Builder<>(subID);
            stateBuilderConsumer.accept(stateBuilder);
            stateBuilders.add(stateBuilder);
            return this;
        }

        @Override
        public ClusterComponentBuilder<D> children(Consumer<ClusterChildComponentBuilder<D>> childComponentBuilderConsumer) {
            childComponentBuilderConsumer.accept(this.childComponentBuilder);
            return this;
        }

        @Override
        public ClusterComponentBuilder<D> entry(String subID) {
            this.entryID = subID;
            return this;
        }

        @Override
        public final Cluster<D> create() {
            BiMap<String, ? extends MenuComponent<D>> children = childComponentBuilder.create();
            Preconditions.checkState(!children.isEmpty(), "Cannot create Cluster without child Components!");
            // Either use explicitly specified entry, or implicitly select it.
            MenuComponent<D> entry;
            if (entryID != null) {
                entry = children.get(entryID);
                Preconditions.checkState(entry != null, "Cannot find entry Component! Please check the explicitly specified entry id!");
            } else {
                entry = children.values().stream().findFirst().orElseThrow();
            }
            return constructImplementation(subID, dataType, wolfyUtils, parent,
                    stateSelector == null ? (holder, data, component) -> 0 : stateSelector,
                    stateBuilders.stream().map(ComponentState.Builder::create).<ComponentState<D>>toArray(ComponentState[]::new),
                    children,
                    entry
            );
        }
        
        protected abstract Cluster<D> constructImplementation(String subID, Class<D> dataType, WolfyUtils wolfyUtils, Cluster<D> parent, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends MenuComponent<D>> children, MenuComponent<D> entry);

    }

    public static abstract class ChildBuilder<D extends Data> implements ClusterChildComponentBuilder<D> {

        private final Cluster<D> parent;
        private final BiMap<String, MenuComponent<D>> children = HashBiMap.create();

        protected ChildBuilder(Cluster<D> parent) {
            this.parent = parent;
        }

        @Override
        public <CT extends Component.Builder<D, ?, ?>> ClusterChildComponentBuilder<D> custom(String subID, NamespacedKey builderId, Class<CT> builderType, Consumer<CT> builderConsumer) {
            // TODO
            return this;
        }

        @Override
        public ClusterChildComponentBuilder<D> window(String id, Consumer<WindowComponentBuilder<D>> windowComponentBuilderConsumer) {
            var windowBuilder = constructWindowBuilderImpl(id, parent);
            windowComponentBuilderConsumer.accept(windowBuilder);
            var window = windowBuilder.create();
            children.put(id, window);
            return this;
        }

        @Override
        public ClusterChildComponentBuilder<D> cluster(String id, Consumer<ClusterComponentBuilder<D>> clusterComponentBuilderConsumer) {
            ClusterComponentBuilder<D> clusterBuilder = constructClusterBuilderImpl(id, parent);
            clusterComponentBuilderConsumer.accept(clusterBuilder);
            var cluster = clusterBuilder.create();
            children.put(id, cluster);
            return this;
        }

        @Override
        public BiMap<String, ? extends MenuComponent<D>> create() {
            return children;
        }

        protected abstract ClusterComponentBuilder<D> constructClusterBuilderImpl(String id, Cluster<D> parent);

        protected abstract WindowComponentBuilder<D> constructWindowBuilderImpl(String id, Cluster<D> parent);
    }
}
