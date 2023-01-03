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

public abstract class WindowCommonImpl<D extends Data> implements Window<D> {

    private final WolfyUtils wolfyUtils;
    private final String id;
    private final MenuComponent<D> parent;
    private final ComponentState<D>[] states;
    private final StateSelector<D> stateSelector;
    private final BiMap<String, ? extends SlotComponent<D>> children;

    protected WindowCommonImpl(String id, Cluster<D> parent, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends SlotComponent<D>> children) {
        this.id = id;
        this.parent = parent;
        this.wolfyUtils = parent.getWolfyUtils();
        this.stateSelector = stateSelector;
        this.states = states;
        this.children = HashBiMap.create(children);
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Set<? extends SlotComponent<D>> children() {
        return children.values();
    }

    @Override
    public MenuComponent<D> parent() {
        return parent;
    }

    @Override
    public Optional<Component<D>> getChild(String id) {
        return Optional.ofNullable(children.get(id));
    }

    @Override
    public ComponentState<D> getState(GuiHolder<D> holder) {
        return states[stateSelector.run(holder, holder.getHandler().getData(), this)];
    }

    public net.kyori.adventure.text.Component onUpdateTitle(GuiHolder<D> holder) {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void open(GuiViewManager<D> handler, UUID player) {
        // This is the final destination and can be opened
        // Start rendering here


    }

    public static abstract class Builder<D extends Data> implements WindowComponentBuilder<D> {

        protected final String subID;
        protected final Cluster<D> parent;
        protected StateSelector<D> stateSelector;
        protected final List<ComponentState.Builder<D, ComponentState<D>>> stateBuilders = new ArrayList<>();
        protected final WindowChildComponentBuilder<D> childComponentBuilder;

        Builder(String subID, Cluster<D> parent) {
            this.subID = subID;
            this.parent = parent;
            this.childComponentBuilder = new ChildBuilder<>(parent);
        }

        @Override
        public Builder<D> stateSelector(StateSelector<D> stateSelector) {
            this.stateSelector = stateSelector;
            return this;
        }

        @Override
        public Builder<D> state(Consumer<ComponentState.Builder<D, ComponentState<D>>> stateBuilderConsumer) {
            ComponentState.Builder<D, ComponentState<D>> stateBuilder = new ComponentStateDefault.Builder<>(subID);
            stateBuilderConsumer.accept(stateBuilder);
            stateBuilders.add(stateBuilder);
            return this;
        }

        @Override
        public Builder<D> children(Consumer<WindowChildComponentBuilder<D>> childComponentBuilderConsumer) {
            childComponentBuilderConsumer.accept(childComponentBuilder);
            return this;
        }

        @Override
        public Window<D> create() {
            return constructImplementation(parent.getID() + "/" + subID,
                    parent,
                    stateSelector == null ? (holder, data, component) -> 0 : stateSelector,
                    stateBuilders.stream().map(ComponentState.Builder::create).<ComponentState<D>>toArray(ComponentState[]::new),
                    childComponentBuilder.create()
            );
        }

        protected abstract Window<D> constructImplementation(String id, Cluster<D> cluster, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends SlotComponent<D>> children);

    }

    static class ChildBuilder<D extends Data> implements WindowChildComponentBuilder<D> {

        private final Cluster<D> parent;
        private final BiMap<String, SlotComponent<D>> children = HashBiMap.create();

        ChildBuilder(Cluster<D> parent) {
            this.parent = parent;
        }

        @Override
        public <CT extends Component.Builder<D, ?, ?>> WindowChildComponentBuilder<D> custom(String subID, NamespacedKey builderId, Class<CT> builderType, Consumer<CT> builderConsumer) {
            // TODO
            return this;
        }

        @Override
        public BiMap<String, ? extends SlotComponent<D>> create() {
            return children;
        }
    }
}
