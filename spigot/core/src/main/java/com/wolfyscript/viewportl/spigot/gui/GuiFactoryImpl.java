/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
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

package com.wolfyscript.viewportl.spigot.gui;

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl;
import com.wolfyscript.viewportl.gui.ViewRuntime;
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler;
import com.wolfyscript.viewportl.gui.rendering.Renderer;
import com.wolfyscript.viewportl.gui.factories.GuiFactory;
import com.wolfyscript.viewportl.spigot.gui.interaction.InventoryGUIInteractionHandler;
import com.wolfyscript.viewportl.spigot.gui.rendering.InventoryGUIRenderer;

public class GuiFactoryImpl implements GuiFactory {

    @Override
    public Renderer<?> createRenderer(ViewRuntime viewRuntime) {
        return new InventoryGUIRenderer((ViewRuntimeImpl) viewRuntime);
    }

    @Override
    public InteractionHandler createInteractionHandler(ViewRuntime viewRuntime) {
        return new InventoryGUIInteractionHandler((ViewRuntimeImpl) viewRuntime);
    }
}
