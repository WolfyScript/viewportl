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

package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class GuiCluster<C extends CustomCache> {

    protected final WolfyUtilities wolfyUtilities;
    protected final InventoryAPI<C> inventoryAPI;
    private String id;
    private final Map<String, Button<C>> buttons;
    private final Map<String, GuiWindow<C>> guiWindows;
    private final ClusterButtonBuilder buttonBuilder;

    private NamespacedKey entry;

    protected GuiCluster(InventoryAPI<C> inventoryAPI, String id) {
        this.inventoryAPI = inventoryAPI;
        this.wolfyUtilities = inventoryAPI.getWolfyUtilities();
        this.id = id;
        this.buttons = new HashMap<>();
        this.guiWindows = new HashMap<>();
        this.entry = null;
        this.buttonBuilder = new ClusterButtonBuilder();
    }

    /**
     * This method is called when the cluster is initialized.
     */
    public abstract void onInit();

    public NamespacedKey getEntry() {
        return entry;
    }

    protected void setEntry(NamespacedKey entry) {
        this.entry = entry;
    }

    protected void registerButton(Button<C> button) {
        button.init(this);
        buttons.putIfAbsent(button.getId(), button);
    }

    private <B extends Button.Builder<C, ?>> void registerButton(B builder) {
        Button<C> button = builder.create();
        buttons.put(button.getId(), button);
    }

    public Button<C> getButton(String id) {
        return buttons.get(id);
    }

    protected void registerGuiWindow(GuiWindow<C> guiWindow) {
        if (this.entry == null) {
            this.entry = guiWindow.getNamespacedKey();
        }
        guiWindow.onInit();
        guiWindows.put(guiWindow.getNamespacedKey().getKey(), guiWindow);
    }

    public GuiWindow<C> getGuiWindow(String id) {
        return guiWindows.get(id);
    }

    /**
     * @return The {@link WolfyUtilities} this cluster belongs to.
     */
    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public InventoryAPI<C> getInventoryAPI() {
        return inventoryAPI;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    Map<String, Button<C>> getButtons() {
        return buttons;
    }

    Map<String, GuiWindow<C>> getGuiWindows() {
        return guiWindows;
    }

    public ButtonBuilder<C> getButtonBuilder() {
        return buttonBuilder;
    }

    public class ClusterButtonBuilder implements ButtonBuilder<C> {

        @Override
        public ChatInputButton.Builder<C> chatInput(String id) {
            return new ChatInputButton.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ActionButton.Builder<C> action(String id) {
            return new ActionButton.Builder<>(GuiCluster.this, id);
        }

        @Override
        public DummyButton.Builder<C> dummy(String id) {
            return new DummyButton.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ItemInputButton.Builder<C> itemInput(String id) {
            return new ItemInputButton.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ToggleButton.Builder<C> toggle(String id) {
            return new ToggleButton.Builder<>(GuiCluster.this, id);
        }
    }
}
