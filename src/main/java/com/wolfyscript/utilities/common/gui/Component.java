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

import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface Component<D extends Data> {

    /**
     * Gets the unique id (in context of the parent) of this component.
     *
     * @return The id of this component.
     */
    String getID();

    /**
     * Gets the global WolfyUtils instance, this component belongs to.
     *
     * @return The WolfyUtils API instance.
     */
    WolfyUtils getWolfyUtils();

    /**
     * The children of this Component; or an empty Set if there are no children.
     *
     * @return The child Components of this Component.
     */
    Set<? extends Component<D>> children();

    /**
     * The parent of this Component, or null if it is a root Component.
     *
     * @return The parent; or null if root Component.
     */
    Component<D> parent();

    /**
     * Gets the child at the relative path from this Component.<br>
     * When the path is null or empty then it returns this Component instead.
     *
     * @param path The path to the child Component.
     * @return The child at the specified path; or this Component when the path is null or empty.
     */
    default Optional<Component<D>> getChild(String... path) {
        if (path == null || path.length == 0) return Optional.of(this);
        return getChild(path[0]).flatMap(component -> component.getChild(Arrays.copyOfRange(path, 1, path.length)));
    }

    /**
     * Gets the direct child Component, or an empty Optional when it wasn't found.
     *
     * @param id The id of the child Component.
     * @return The child Component; or empty Component.
     */
    Optional<Component<D>> getChild(String id);

    /**
     * The state of this Component for the specified
     * {@link GuiHolder <D>} (e.g. Data, Permission, etc.)
     *
     * @param holder The holder to select the state for.
     * @return The selected state for the holder.
     */
    ComponentState<D> getState(GuiHolder<D> holder);

    /**
     * Called when the Component is initialised.
     *
     */
    void init();

    default Deque<Component<D>> getPathToRoot() {
        if (parent() == null) return new ArrayDeque<>();
        Deque<Component<D>> path = parent().getPathToRoot();
        path.add(parent());
        return path;
    }

    /**
     * Called when an interaction occurs inside the Component.<br>
     * This may be called if a child Component is interacted with, for example a Button will cause this interaction to<br>
     * propagate from the root Cluster, down the Windows to the Button that caused the interaction to be called.<br>
     * <br>
     * For this behaviour any implementation must first call the parent interaction, before continuing.<br>
     * Only if there is no parent available (root Component) it continues, going back to the interaction cause.<br>
     *
     * @param holder             The holder that caused the interaction.
     * @param data               The data of the handler (for convenience)
     * @param interactionDetails The details about the interaction.
     * @return The interaction result.
     */
    default InteractionResult interact(GuiHolder<D> holder, D data, InteractionDetails<D> interactionDetails) {
        if (parent() != null) {
            InteractionResult result = parent().interact(holder, data, interactionDetails);
            if (result.isCancelled()) return result;
        }
        return getState(holder).interactCallback().interact(holder, data, this, interactionDetails);
    }

    /**
     * Called whenever a Component is rendered.
     *
     * @param holder
     * @param data
     * @param context
     */
    default void render(GuiHolder<D> holder, D data, RenderContext<D> context) {
        if (parent() != null) parent().render(holder, data, context);
        getState(holder).renderCallback().render(holder, data, this, context);
    }

    /**
     * Builder used to create Components.<br>
     * A Builder should always be preferred over creating Components via constructor.
     *
     * @param <D>
     * @param <C>
     */
    interface Builder<D extends Data, C extends Component<D>, CB extends ChildComponentBuilder<D>, SB extends ComponentState.Builder<D, ?>> {

        /**
         * Specifies the StateSelector to use to select the Components state based on
         * current data and holder.
         *
         * @param stateSelector The state selector to use.
         * @return This builder for chaining.
         */
        Builder<D, C, CB, SB> stateSelector(StateSelector<D> stateSelector);

        /**
         * Specifies the state of the Component.<br>
         * Depending on the owning Component this may set or add the state.<br>
         * For example, some Components can have multiple states that are selected using the state selector specified using {@link #stateSelector(StateSelector)}.<br>
         * In these cases the states are added in the order of invocations of this method.<br>
         * <br>
         * The consumer must not call {@link ComponentState.Builder#create()} as that is handled by the implementation of this builder!
         *
         * @param builderConsumer The consumer that provides an instance of a new {@link ComponentState.Builder}
         * @return This builder for chaining.
         */
        Builder<D, C, CB, SB> state(Consumer<SB> builderConsumer);

        /**
         * The ChildComponentBuilder is used to create and register
         * child Components for this Component.
         *
         * @return The ChildComponentBuilder of this Component.
         */
        Component.Builder<D, C, CB, SB> children(Consumer<CB> childComponentBuilder);

        /**
         * Creates the Component this builder belongs to.
         *
         * @return The new instance of the Component.
         */
        C create();

    }

}
