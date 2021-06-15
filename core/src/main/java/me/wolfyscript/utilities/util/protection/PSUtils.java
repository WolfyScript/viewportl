package me.wolfyscript.utilities.util.protection;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.plot.Plot;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PSUtils {

    //PlotSquared API Utils
    private static final PlotAPI plotAPI = new PlotAPI();

    public static Plot getPlot(org.bukkit.Location location) {
        return BukkitUtil.getLocation(location).getPlot();
    }

    public static boolean hasPerm(Player player, org.bukkit.Location location) {
        var location1 = BukkitUtil.getLocation(location);
        if (!isPlotWorld(player.getWorld())) {
            return true;
        }
        var plot = location1.getPlot();
        return hasPlotPerm(player.getUniqueId(), plot);
    }

    public static boolean isPlotWorld(World world) {
        return plotAPI.getPlotSquared().hasPlotArea(world.getName());
    }

    public static boolean hasPlotPerm(UUID uuid, Plot plot) {
        return plot != null && plot.isAdded(uuid);
    }


}
