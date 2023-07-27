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
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface Window extends Interactable, Renderable {

    /**
     * Creates the context of this window. The context keeps track of the inventory
     * and view for the rendering.
     *
     * @param viewManager The view manager to open.
     * @param uuid The uuid to open the Window for.
     */
    RenderContext createContext(GuiViewManager viewManager, UUID uuid);

    void open(GuiViewManager viewManager);

    Window construct(GuiHolder holder, GuiViewManager viewManager);

    void render(GuiHolder holder, GuiViewManager viewManager, RenderContext context);

    /**
     * Gets the type that is configured for this Window.<br>
     * <b>When this is empty, then {@link #getSize()} will return the specified size.</b>
     *
     * @return The specified type; or empty Optional when no type is configured.
     * @see #getSize()
     */
    Optional<WindowType> getType();

    /**
     * Gets the size that is configured for this Window.<br>
     *
     * <b>When this is empty, then {@link #getType()} will return the specified type.</b>
     *
     * @return The specified size: or empty Optional when no size is configured.
     */
    Optional<Integer> getSize();

    /**
     * Creates the title of this window for the specified holder.
     *
     * @param holder The holder to create the title for.
     * @return The title component.
     */
    net.kyori.adventure.text.Component createTitle(GuiHolder holder);

    /**
     * The children of this Component; or an empty Set if there are no children.
     *
     * @return The child Components of this Component.
     */
    Set<? extends Component> childComponents();

    /**
     * Gets the child at the relative path from this Component.<br>
     * When the path is null or empty then it returns this Component instead.
     *
     * @param path The path to the child Component.
     * @return The child at the specified path; or this Component when the path is null or empty.
     */
    default Optional<? extends Component> getChild(String... path) {
        if (path == null || path.length == 0) return Optional.empty();
        return getChild(path[0]).flatMap(component -> {
            if (component instanceof Window window) {
                return window.getChild(Arrays.copyOfRange(path, 1, path.length));
            }
            return Optional.empty();
        });
    }

    /**
     * Gets the direct child Component, or an empty Optional when it wasn't found.
     *
     * @param id The id of the child Component.
     * @return The child Component; or empty Component.
     */
    Optional<? extends Component> getChild(String id);

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
     * The parent of this Component, or null if it is a root Component.
     *
     * @return The parent; or null if root Component.
     */
    Router router();

    /**
     * Gets the width of this Component in slot count.
     *
     * @return The width in slots.
     */
    int width();

    /**
     * Gets the width of this Component in slot count.
     *
     * @return The height in slots.
     */
    int height();

    default void executeForAllSlots(int positionSlot, Consumer<Integer> slotFunction) {
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                slotFunction.accept(positionSlot + j + i * (9 - width()));
            }
        }
    }

}
