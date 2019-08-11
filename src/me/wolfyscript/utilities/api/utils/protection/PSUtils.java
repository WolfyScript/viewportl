package me.wolfyscript.utilities.api.utils.protection;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PSUtils {

    //PlotSquared API Utils

    private static PlotAPI plotAPI = new PlotAPI();

    public static boolean hasPerm(Player player, Location location) {
        Plot plot = plotAPI.getPlot(location);
        if (!isPlotWorld(player.getWorld()))
            return true;
        return hasPlotPerm(player.getUniqueId(), plot);
    }

    public static boolean isPlotWorld(World world) {
        return plotAPI.isPlotWorld(world);
    }

    public static boolean hasPlotPerm(UUID uuid, Plot plot) {
        return plot != null && (plot.getTrusted().contains(uuid) || plot.isOwner(uuid) || plot.getMembers().contains(uuid) || plot.isAdded(uuid));
    }

}
