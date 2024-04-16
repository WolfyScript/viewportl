package com.wolfyscript.utilities.paper;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommonBootstrap;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;

/**
 * This abstract class is the actual core of the plugin (This class is being extended by the plugin instance).<br>
 * <p>
 * It provides access to internal functionality like {@link BukkitRegistries}, {@link CompatibilityManagerBukkit}, and of course the creation of the API instance.<br>
 * <p>
 * To get an instance of the API ({@link WolfyUtilsBukkit}) for your plugin you need one of the following methods. <br>
 * </p>
 */
public final class WolfyCorePaperBootstrap extends WolfyCoreCommonBootstrap {

    private final WolfyCorePaper core;

    public WolfyCorePaperBootstrap() {
        super();
        this.core = new WolfyCorePaper(this);
    }

    public WolfyCorePaper getCore() {
        return core;
    }

}
