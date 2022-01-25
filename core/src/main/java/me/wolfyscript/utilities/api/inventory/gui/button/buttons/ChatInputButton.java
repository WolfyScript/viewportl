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

package me.wolfyscript.utilities.api.inventory.gui.button.buttons;

import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ChatImpl;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.*;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public class ChatInputButton<C extends CustomCache> extends ActionButton<C> {

    private final ChatInputAction<C> action;
    private final ChatTabComplete<C> tabComplete;
    private String msg = "";
    private boolean global = false;
    private ClickData clickData = null;

    public ChatInputButton(String id, ButtonState<C> buttonState, String msg, ChatInputAction<C> action) {
        this(id, buttonState, msg, action, null);
    }

    public ChatInputButton(String id, ButtonState<C> buttonState, String msg, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        super(id, buttonState);
        this.action = action;
        this.tabComplete = tabComplete;
        this.msg = msg;
    }

    public ChatInputButton(String id, ItemStack itemStack, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack), msg, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, String msg, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        this(id, new ButtonState<>(id, itemStack), msg, action, tabComplete);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction), msg, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonRender<C> render, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, render), msg, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ButtonRender<C> render, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction, render), msg, action);
    }

    public ChatInputButton(String id, ButtonState<C> buttonState, ChatInputAction<C> action) {
        this(id, buttonState, action, null);
    }

    public ChatInputButton(String id, ButtonState<C> buttonState, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        super(id, buttonState);
        this.action = action;
        this.tabComplete = tabComplete;
    }

    public ChatInputButton(String id, ItemStack itemStack, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack), action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        this(id, new ButtonState<>(id, itemStack), action, tabComplete);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction), action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, render), action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction, render), action);
    }

    public ChatInputButton(String id, Material material, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material), action);
    }

    public ChatInputButton(String id, Material material, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        this(id, new ButtonState<>(id, material), action, tabComplete);
    }

    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction), action);
    }

    public ChatInputButton(String id, Material material, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, render), action);
    }

    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction, render), action);
    }

    public ChatInputButton(String id, ButtonState<C> buttonState, ClickData clickData, ChatInputAction<C> action) {
        this(id, buttonState, clickData, action, null);
    }

    public ChatInputButton(String id, ButtonState<C> buttonState, ClickData clickData, ChatInputAction<C> chatInput, ChatTabComplete<C> tabComplete) {
        super(id, buttonState);
        this.action = chatInput;
        this.tabComplete = tabComplete;
        this.clickData = clickData;
    }

    public ChatInputButton(String id, ItemStack itemStack, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack), clickData, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, render), clickData, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction), clickData, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction, render), clickData, action);
    }

    public ChatInputButton(String id, Material material, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material), clickData, action);
    }

    public ChatInputButton(String id, Material material, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, render), clickData, action);
    }

    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction), clickData, action);
    }

    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction, render), clickData, action);
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        super.init(guiWindow);
        this.global = false;
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        super.init(guiCluster);
        this.global = true;
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        //If the ButtonAction returns true then the ChatInput will be created.
        if (super.execute(guiHandler, player, inventory, slot, event)) {
            guiHandler.setChatTabComplete(tabComplete);
            guiHandler.setChatInputAction(action);
            Chat chat = guiHandler.getApi().getChat();
            if (!msg.isEmpty()) {
                chat.sendMessage(guiHandler.getPlayer(), msg);
            } else if (clickData != null) {
                guiHandler.getApi().getChat().sendActionMessage(guiHandler.getPlayer(), clickData);
            } else {
                if (global) {
                    chat.sendMessage(player, "$inventories." + guiHandler.getCluster().getId() + ".global_items." + getId() + ".message$");
                } else if (guiHandler.getWindow() != null) {
                    chat.sendMessage(player, "$inventories." + guiHandler.getCluster().getId() + "." + guiHandler.getWindow().getNamespacedKey().getKey() + ".items." + getId() + ".message$");
                }
            }
            guiHandler.close();
        }
        //If the ButtonAction returns false then the ChatInput won't be created.
        return true; //The click is always cancelled.
    }
}
