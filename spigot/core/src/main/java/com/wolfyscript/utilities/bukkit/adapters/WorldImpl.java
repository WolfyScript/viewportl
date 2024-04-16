package com.wolfyscript.utilities.bukkit.adapters;

import com.wolfyscript.utilities.platform.adapters.Block;
import com.wolfyscript.utilities.platform.adapters.Location;
import com.wolfyscript.utilities.platform.adapters.World;
import org.jetbrains.annotations.NotNull;

public class WorldImpl extends BukkitRefAdapter<org.bukkit.World> implements World {

    WorldImpl(org.bukkit.World bukkitRef) {
        super(bukkitRef);
    }

    @Override
    public @NotNull Block getBlockAt(int i, int i1, int i2) {
        return null;
    }

    @Override
    public Block getBlockAt(Location location) {
        return null;
    }
}
