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

package me.wolfyscript.utilities.api;

import org.bukkit.command.CommandSender;

import java.util.Locale;

public class Permissions {

    private final WolfyUtilities wolfyUtilities;
    private String permissionKey;

    public Permissions(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.permissionKey = wolfyUtilities.getPlugin().getName().toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    /**
     * This permission key is the first key of the permission.<br>
     * For example <b><i>wolfyutilities.*</i></b>
     *
     * @return the first permission key
     */
    public String getPermissionKey() {
        return permissionKey;
    }

    /**
     * @param permissionKey
     */
    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public boolean hasPermission(CommandSender sender, String permCode) {
        if (sender.hasPermission("*")) return true;
        StringBuilder permission = new StringBuilder();
        for (String s : permCode.split("\\.")) {
            permission.append(s);
            if (sender.hasPermission(permission.toString()) || sender.hasPermission(permission.toString() + ".*")) {
                return true;
            }
            permission.append(".");
        }
        return false;
    }
}
