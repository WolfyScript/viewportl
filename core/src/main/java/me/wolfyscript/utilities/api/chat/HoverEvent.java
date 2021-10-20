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

package me.wolfyscript.utilities.api.chat;

import me.wolfyscript.utilities.api.WolfyUtilities;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.inventory.ItemStack;

public class HoverEvent implements ChatEvent<net.md_5.bungee.api.chat.HoverEvent.Action, BaseComponent[]> {

    private final net.md_5.bungee.api.chat.HoverEvent.Action action;
    private final BaseComponent[] value;

    public HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action action, BaseComponent[] value) {
        this.action = action;
        this.value = value;
    }

    public HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action action, String value) {
        this(action, new ComponentBuilder(value).create());
    }

    public HoverEvent(ItemStack itemStack) {
        this(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(WolfyUtilities.getWUCore().getNmsUtil().getItemUtil().getItemStackJson(itemStack)).create());
    }

    public HoverEvent(Action action, BaseComponent[] value) {
        this(action.getAction(), value);
    }

    public HoverEvent(Action action, String value) {
        this(action, new ComponentBuilder(value).create());
    }

    @Override
    public BaseComponent[] getValue() {
        return value;
    }

    @Override
    public net.md_5.bungee.api.chat.HoverEvent.Action getAction() {
        return action;
    }

    public enum Action {
        SHOW_TEXT,
        SHOW_ACHIEVEMENT,
        SHOW_ITEM,
        SHOW_ENTITY;

        public net.md_5.bungee.api.chat.HoverEvent.Action getAction() {
            return net.md_5.bungee.api.chat.HoverEvent.Action.valueOf(this.toString());
        }
    }
}
