package com.wolfyscript.utilities.bukkit.adapters;

import com.wolfyscript.utilities.platform.adapters.Entity;
import com.wolfyscript.utilities.platform.adapters.Location;
import com.wolfyscript.utilities.platform.adapters.Player;
import com.wolfyscript.utilities.platform.adapters.World;

public class BukkitWrapper {

    public static Location adapt(org.bukkit.Location bukkitLoc) {
        return new LocationImpl(bukkitLoc);
    }

    public static Entity adapt(org.bukkit.entity.Entity bukkitEntity) {
        return new EntityImpl<>(bukkitEntity);
    }

    public static World adapt(org.bukkit.World world) {
        return new WorldImpl(world);
    }

    public static Player adapt(org.bukkit.entity.Player player) {
        return new PlayerImpl(player);
    }
}
