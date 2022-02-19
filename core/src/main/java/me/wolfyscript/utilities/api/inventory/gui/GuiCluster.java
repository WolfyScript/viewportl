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
import net.kyori.adventure.text.minimessage.Template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The GuiCluster combines multiple child <b>{@link GuiWindow}s</b>.<br>
 * You can think of it as a namespace of GuiWindows.<br>
 *
 * Buttons registered in a GuiCluster are available globally, so you can use them in the child windows or access them in windows outside this cluster.<br>
 *
 * @param <C> The type of the CustomCache
 */
public abstract class GuiCluster<C extends CustomCache> extends GuiMenuComponent<C> {

    protected final InventoryAPI<C> inventoryAPI;
    private final String id;
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

    /**
     * Gets the entrypoint of this cluster.
     *
     * @return The namespaced key of the entrypoint window.
     */
    public NamespacedKey getEntry() {
        return entry;
    }

    /**
     * Sets the {@link GuiWindow} entrypoint of this cluster.
     *
     * @param entry The namespaced key of the {@link GuiWindow}. See {@link GuiWindow#getNamespacedKey()}
     */
    protected void setEntry(NamespacedKey entry) {
        this.entry = entry;
    }

    /**
     * Registers the button in this cluster.
     *
     * @param button The button to register.
     */
    public void registerButton(Button<C> button) {
        button.init(this);
        buttons.putIfAbsent(button.getId(), button);
    }

    /**
     * Registers a {@link GuiWindow} in this cluster.<br>
     * In case the entrypoint isn't set at the time of the registration, it'll set this window as the entry.
     *
     * @param guiWindow The GuiWindow to register.
     */
    protected void registerGuiWindow(GuiWindow<C> guiWindow) {
        if (this.entry == null) {
            this.entry = guiWindow.getNamespacedKey();
        }
        guiWindow.onInit();
        guiWindows.put(guiWindow.getNamespacedKey().getKey(), guiWindow);
    }

    /**
     * Gets the child {@link GuiWindow} by its id.
     *
     * @param id The id of the child window.
     * @return The GuiWindow of the id; otherwise null if there is no GuiWindow for that id.
     */
    public GuiWindow<C> getGuiWindow(String id) {
        return guiWindows.get(id);
    }

    /**
     * Gets the id of the GuiCluster.
     *
     * @return The id of the cluster.
     */
    public String getId() {
        return id;
    }

    Map<String, GuiWindow<C>> getGuiWindows() {
        return guiWindows;
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param templates The placeholders and values in the message.
     * @param translateLegacyColor If it should translate legacy '&' color codes.
     * @return The component set for the key; empty component if not available.
     */
    @Override
    public Component translatedMsgKey(String key, boolean translateLegacyColor, List<Template> templates) {
        return getChat().translated("inventories." + id + ".global_messages." + key, translateLegacyColor, templates);
    }

    /**
     * The button builder for this GuiCluster. It creates new instances of the builders using the instance of this GuiCluster.<br>
     * Therefor calling the {@link Button.Builder#register()} will then register the button into this GuiCluster.
     */
    protected class ClusterButtonBuilder implements ButtonBuilder<C> {

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
