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

package me.wolfyscript.utilities.util.protection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WGUtils {

    public static WorldGuardPlugin inst = WorldGuardPlugin.inst();

    public static boolean teleportEntity(Entity entity, Location location, Player player) {
        if (hasPermBuild(location, player)) {
            entity.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            return true;
        }
        return false;
    }

    public static boolean hasPermBuild(Location location, Player player) {
        return hasPermBuild(location, player, Flags.BUILD);
    }

    public static boolean hasPermBuild(Location location, Player player, StateFlag... flag) {
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        var localPlayer = inst.wrapPlayer(player);
        return set.testState(localPlayer, flag) || hasBypassPerm(player, location);
    }

    public static boolean hasBypassPerm(Player player, Location location) {
        return hasBypassPerm(player, location.getWorld());
    }

    public static boolean hasBypassPerm(Player player, World world) {
        return player.hasPermission("worldguard.region.bypass.*") || player.hasPermission("worldguard.region.bypass." + world.getName());
    }

}
