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
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class GuiMenuComponent<C extends CustomCache> {

    public final WolfyUtilities wolfyUtilities;
    protected final InventoryAPI<C> inventoryAPI;
    ButtonBuilder<C> buttonBuilder;

    final HashMap<String, Button<C>> buttons = new HashMap<>();

    protected GuiMenuComponent(InventoryAPI<C> inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
        this.wolfyUtilities = inventoryAPI.getWolfyUtilities();
    }

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

    Map<String, Button<C>> getButtons() {
        return buttons;
    }

    /**
     * Creates a new Component of the given language message key.
     *
     * @param key The key of the message in the language.
     * @return The translated Component of that message; Or empty Component if non-existing.
     */
    abstract Component translatedMsgKey(String key);

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
}
