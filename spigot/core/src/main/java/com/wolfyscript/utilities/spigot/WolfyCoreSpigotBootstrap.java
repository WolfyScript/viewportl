package com.wolfyscript.utilities.spigot;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommonBootstrap;

public final class WolfyCoreSpigotBootstrap extends WolfyCoreCommonBootstrap {

    private final WolfyCoreSpigot core;

    public WolfyCoreSpigotBootstrap() {
        super();
        this.core = new WolfyCoreSpigot(this);
    }

    public WolfyCoreSpigot getCore() {
        return core;
    }

}
