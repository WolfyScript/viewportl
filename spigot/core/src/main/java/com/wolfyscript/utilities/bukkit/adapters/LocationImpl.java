package com.wolfyscript.utilities.bukkit.adapters;

import com.wolfyscript.utilities.platform.adapters.Location;

public class LocationImpl extends BukkitRefAdapter<org.bukkit.Location> implements Location {

    public LocationImpl(org.bukkit.Location bukkitRef) {
        super(bukkitRef);
    }

}
