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

public abstract class WindowCommonImpl<D extends Data> implements Window<D> {

    protected final WolfyUtils wolfyUtils;
    protected final String id;
    protected final MenuComponent<D> parent;
    protected final WindowState<D>[] states;
    protected final StateSelector<D> stateSelector;
    protected final BiMap<String, ? extends SlotComponent<D>> children;
    protected final Integer size;
    protected final WindowType type;
    protected final WindowTitleUpdateCallback<D> titleUpdateCallback;

    protected WindowCommonImpl(String id, Cluster<D> parent, WindowTitleUpdateCallback<D> titleUpdateCallback, StateSelector<D> stateSelector, WindowState<D>[] states, BiMap<String, ? extends SlotComponent<D>> children, Integer size, WindowType type) {
        this.id = id;
        this.parent = parent;
        this.titleUpdateCallback = titleUpdateCallback;
        this.wolfyUtils = parent.getWolfyUtils();
        this.stateSelector = stateSelector;
        this.states = states;
        this.children = HashBiMap.create(children);
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!");
        this.size = size;
        this.type = type;
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
    public WindowState<D> getState(GuiHolder<D> holder) {
        return states[stateSelector.run(holder, holder.getViewManager().getData(), this)];
    }

    @Override
    public Optional<Integer> getSize() {
        return Optional.ofNullable(size);
    }

    @Override
    public Optional<WindowType> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public net.kyori.adventure.text.Component createTitle(GuiHolder<D> holder) {
        return titleUpdateCallback.run(holder, holder.getViewManager().getData(), this);
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
        protected final List<WindowStateBuilder<D>> stateBuilders = new ArrayList<>();
        protected final WindowChildComponentBuilder<D> childComponentBuilder;
        protected Integer size;
        protected WindowType type;
        protected WindowTitleUpdateCallback<D> titleUpdateCallback;

        protected Builder(String subID, Cluster<D> parent, WindowChildComponentBuilder<D> childComponentBuilder) {
            this.subID = subID;
            this.parent = parent;
            this.childComponentBuilder = childComponentBuilder;
        }

        @Override
        public Builder<D> size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public Builder<D> type(WindowType type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder<D> title(WindowTitleUpdateCallback<D> titleUpdateCallback) {
            this.titleUpdateCallback = titleUpdateCallback;
            return this;
        }

        @Override
        public Builder<D> stateSelector(StateSelector<D> stateSelector) {
            this.stateSelector = stateSelector;
            return this;
        }

        @Override
        public abstract Builder<D> state(Consumer<WindowStateBuilder<D>> stateBuilderConsumer);

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
                    stateBuilders.stream().map(WindowStateBuilder::create).<WindowState<D>>toArray(WindowState[]::new),
                    childComponentBuilder.create(),
                    size, type
            );
        }

        protected abstract Window<D> constructImplementation(String id, Cluster<D> cluster, StateSelector<D> stateSelector, WindowState<D>[] states, BiMap<String, ? extends SlotComponent<D>> children, Integer size, WindowType type);

    }

    public static class ChildBuilder<D extends Data> implements WindowChildComponentBuilder<D> {

        private final Cluster<D> parent;
        private final BiMap<String, SlotComponent<D>> children = HashBiMap.create();

        protected ChildBuilder(Cluster<D> parent) {
            this.parent = parent;
        }

        @Override
        public <CT extends Component.Builder<D, ?, ?, ?>> WindowChildComponentBuilder<D> custom(String subID, NamespacedKey builderId, Class<CT> builderType, Consumer<CT> builderConsumer) {
            // TODO
            return this;
        }

        @Override
        public BiMap<String, ? extends SlotComponent<D>> create() {
            return children;
        }
    }
}
