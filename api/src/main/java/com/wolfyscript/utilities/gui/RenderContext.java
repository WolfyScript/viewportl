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

package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.platform.adapters.ItemStack;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.UUID;

/**
 * The data that contains all the information needed to render the Menu.
 *
 */
public interface RenderContext {

    GuiHolder holder();

    void enterNode(Component component);

    void exitNode();

    Component getCurrentComponent();

    int currentOffset();

    void setStack(int slot, ItemStackConfig stackConfig);

    void renderStack(Position position, ItemStack itemStack);

    void renderStack(Position position, ItemStackConfig itemStackConfig, ItemStackContext itemStackContext);

    void updateTitle(GuiHolder holder, net.kyori.adventure.text.Component component);

    ItemStackContext createContext(GuiHolder guiHolder, TagResolver tagResolvers);

    /**
     *
     *
     * @param viewer
     */
    void openAndRenderMenuFor(GuiViewManager viewManager, UUID viewer);

    default boolean checkIfSlotInBounds(int slot) {
        int outerWidth;
        int outerHeight;
        if (getCurrentComponent().parent() != null) {
            Component parent = getCurrentComponent().parent();
            outerWidth = parent.width();
            outerHeight = parent.height();
        } else {
            outerWidth = 9;
            outerHeight = 6;
        }
        if (slot >= 0 && slot < outerWidth * outerHeight) {
            return true;
        }
        throw new IllegalArgumentException("Slot " + slot + " out of bounds! Must be in the range of [" + 0 + "..." + (outerWidth * outerHeight - 1) + "] !");
    }

}
