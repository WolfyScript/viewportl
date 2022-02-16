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
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a GUI menu, that is linked to the InventoryAPI.<br>
 * It is possible to register Buttons and to open the chat for a player to request input.
 * <br>
 * Classes that are GUI menus:
 * <ul>
 *     <li>{@link GuiCluster}</li>
 *     <li>{@link GuiWindow}</li>
 * </ul>
 *
 * @param <C> The type of the CustomCache
 */
public abstract class GuiMenuComponent<C extends CustomCache> {

    public final WolfyUtilities wolfyUtilities;
    protected final InventoryAPI<C> inventoryAPI;
    ButtonBuilder<C> buttonBuilder;

    final HashMap<String, Button<C>> buttons = new HashMap<>();

    protected GuiMenuComponent(InventoryAPI<C> inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
        this.wolfyUtilities = inventoryAPI.getWolfyUtilities();
    }

    /**
     * Gets the InventoryAPI of this GuiMenu.
     *
     * @return The InventoryAPI
     */
    public InventoryAPI<C> getInventoryAPI() {
        return inventoryAPI;
    }

    /**
     * @return The {@link WolfyUtilities} that this window belongs to.
     */
    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    /**
     * @return The Chat of the API.
     */
    public Chat getChat() {
        return wolfyUtilities.getChat();
    }

    public ButtonBuilder<C> getButtonBuilder() {
        return buttonBuilder;
    }

    /**
     * @param id The id of the button.
     * @return The button if it exists, else null.
     */
    @Nullable
    public final Button<C> getButton(String id) {
        return buttons.get(id);
    }

    /**
     * @param id The id of the button.
     * @return If the button exists. True if it exists, else false.
     */
    public final boolean hasButton(String id) {
        return buttons.containsKey(id);
    }

    /**
     * Gets all the registered Buttons.<br>
     * For internal use only.
     *
     * @return The map of the registered buttons.
     */
    Map<String, Button<C>> getButtons() {
        return buttons;
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @return The component set for the key; empty component if not available.
     */
    public Component translatedMsgKey(String key) {
        return translatedMsgKey(key, false, List.of());
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param translateLegacyColor If it should translate legacy '&' color codes.
     * @return The component set for the key; empty component if not available.
     */
    public Component translatedMsgKey(String key, boolean translateLegacyColor) {
        return translatedMsgKey(key, translateLegacyColor, List.of());
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param templates The placeholders and values in the message.
     * @return The component set for the key; empty component if not available.
     */
    public Component translatedMsgKey(String key, List<Template> templates) {
        return translatedMsgKey(key, false, templates);
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
    public abstract Component translatedMsgKey(String key, boolean translateLegacyColor, List<Template> templates);

    /**
     * Opens the chat, send the player the defined message and waits for the input of the player.
     * When the player sends a message the inputAction method is executed.
     *
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param msg         The message that should be sent to the player.
     * @param inputAction The {@link ChatInputAction} to be executed when the player types in the chat.
     */
    public void openChat(GuiHandler<C> guiHandler, Component msg, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        getChat().sendMessage(guiHandler.getPlayer(), msg);
    }

    public void sendMessage(GuiHandler<C> guiHandler, Component msg) {
        getChat().sendMessage(guiHandler.getPlayer(), msg);
    }

    /**
     * Interface that contains methods to create new button builders.<br>
     * It is implemented by either {@link GuiWindow} or {@link GuiCluster} and will create the builders accordingly.
     * Calling the {@link Button.Builder#register()} will then register the button either into the {@link GuiWindow} or {@link GuiCluster} depending on from which one the builder was created.
     *
     * @param <C> The type of the custom cache.
     */
    protected interface ButtonBuilder<C extends CustomCache> {

        /**
         * Gets a new builder for a {@link ChatInputButton <C>}.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ChatInputButton.Builder<C> chatInput(String id);

        /**
         * Gets a new builder for a {@link ActionButton <C>}.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ActionButton.Builder<C> action(String id);

        /**
         * Gets a new builder for a {@link DummyButton <C>}.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        DummyButton.Builder<C> dummy(String id);

        /**
         * Gets a new builder for a {@link ItemInputButton <C>}.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ItemInputButton.Builder<C> itemInput(String id);

        /**
         * Gets a new builder for a {@link ToggleButton <C>}.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ToggleButton.Builder<C> toggle(String id);

        /**
         * Gets a new builder for a {@link MultipleChoiceButton <C>}.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        MultipleChoiceButton.Builder<C> multiChoice(String id);

    }
}
