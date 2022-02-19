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

@Deprecated
public class ClickEvent implements ChatEvent<net.md_5.bungee.api.chat.ClickEvent.Action, String> {

    private final net.md_5.bungee.api.chat.ClickEvent.Action action;
    private final String value;

    public ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public ClickEvent(Action action, String value) {
        this(action.getAction(), value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public net.md_5.bungee.api.chat.ClickEvent.Action getAction() {
        return action;
    }

    public enum Action {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE;

        public net.md_5.bungee.api.chat.ClickEvent.Action getAction() {
            return net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(this.toString());
        }
    }
}
