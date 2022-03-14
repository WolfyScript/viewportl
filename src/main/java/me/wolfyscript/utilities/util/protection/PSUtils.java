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

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PSUtils {

    //PlotSquared API Utils
    private static final PlotAPI plotAPI = new PlotAPI();

    public static Plot getPlot(org.bukkit.Location location) {
        return BukkitUtil.adapt(location).getPlot();
    }

    public static boolean hasPerm(Player player, org.bukkit.Location location) {
        var location1 = BukkitUtil.adapt(location);
        if (!isPlotWorld(player.getWorld())) {
            return true;
        }
        var plot = location1.getPlot();
        return hasPlotPerm(player.getUniqueId(), plot);
    }

    public static boolean isPlotWorld(World world) {
        return !plotAPI.getPlotAreas(world.getName()).isEmpty();
    }

    public static boolean hasPlotPerm(UUID uuid, Plot plot) {
        return plot != null && plot.isAdded(uuid);
    }


}
