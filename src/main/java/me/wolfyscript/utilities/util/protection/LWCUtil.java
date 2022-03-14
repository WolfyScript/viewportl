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

import com.griefcraft.lwc.LWC;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LWCUtil {

    //LWC Utils! Only tested for the LWC fork by Brokkonaut!

    private static final LWC lwc = LWC.getInstance();

    public static boolean hasPermToInteract(Player player, Entity entity) {
        return lwc.canAccessProtection(player, entity.getLocation().getBlock());
    }

    public static boolean canAccessprotection(Player player, Block block) {
        return lwc.canAccessProtection(player, block);
    }

}
