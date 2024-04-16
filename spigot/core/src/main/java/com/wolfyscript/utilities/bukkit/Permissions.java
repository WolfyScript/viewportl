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

package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.WolfyUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class Permissions {

    private final WolfyUtils wolfyUtilities;
    private Permission rootPermission;

    public Permissions(WolfyUtils wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.rootPermission = null;
    }

    public void initRootPerm(String permName) {
        var rootPerm = Bukkit.getPluginManager().getPermission(permName);
        if (rootPerm == null) {
            rootPerm = new Permission(permName);
            Bukkit.getPluginManager().addPermission(rootPerm);
        }
        this.rootPermission = rootPerm;
    }

    public Permission getRootPermission() {
        return rootPermission;
    }

    /**
     * This permission key is the first key of the permission.<br>
     * For example <b><i>wolfyutilities.*</i></b>
     *
     * @return the first permission key
     */
    @Deprecated
    public String getPermissionKey() {
        return rootPermission.getName();
    }

    /**
     * @param permissionKey
     */
    @Deprecated
    public void setPermissionKey(String permName) {
        initRootPerm(permName);
    }

    /**
     * @deprecated Just use {@link CommandSender#hasPermission(String)}
     */
    @Deprecated
    public boolean hasPermission(CommandSender sender, String permCode) {
        return sender.hasPermission(permCode);
    }
}
