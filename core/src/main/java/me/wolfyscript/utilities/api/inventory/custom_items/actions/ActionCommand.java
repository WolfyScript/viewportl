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

package me.wolfyscript.utilities.api.inventory.custom_items.actions;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ActionCommand extends Action<ActionDataPlayer> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("command");

    private String command;

    protected ActionCommand() {
        super(KEY, ActionDataPlayer.class);
    }

    @Override
    public void execute(WolfyUtilCore core, ActionDataPlayer data) {
        if (data.getPlayer() != null) {
            data.getPlayer().performCommand(command);
        }
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
