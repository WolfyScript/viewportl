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

import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

public abstract class GuiCluster<C extends CustomCache> extends GuiMenuComponent<C> {

    protected final InventoryAPI<C> inventoryAPI;
    private String id;
    private final Map<String, GuiWindow<C>> guiWindows;

    private NamespacedKey entry;

    protected GuiCluster(InventoryAPI<C> inventoryAPI, String id) {
        super(inventoryAPI);
        this.inventoryAPI = inventoryAPI;
        this.id = id;
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

    public void registerButton(Button<C> button) {
        button.init(this);
        buttons.putIfAbsent(button.getId(), button);
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

    void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    Map<String, GuiWindow<C>> getGuiWindows() {
        return guiWindows;
    }

    @Override
    public Component translatedMsgKey(String key) {
        return getChat().translated("inventories." + id + ".global_messages." + key);
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

        @Override
        public MultipleChoiceButton.Builder<C> multiChoice(String id) {
            return new MultipleChoiceButton.Builder<>(GuiCluster.this, id);
        }
    }
}
