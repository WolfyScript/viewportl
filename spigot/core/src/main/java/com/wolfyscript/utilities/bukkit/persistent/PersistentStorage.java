package com.wolfyscript.utilities.bukkit.persistent;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The PersistentStorage API allows plugins to store custom complex data into block/chunks and players.<br>
 * It uses the {@link org.bukkit.persistence.PersistentDataHolder} and {@link org.bukkit.persistence.PersistentDataContainer} API to store the data.<br>
 * <br>
 * This class keeps track of the cached storage instances of worlds and players, and possibly more.
 *
 */
public class PersistentStorage {

    private final Map<UUID, WorldStorage> WORLD_STORAGE = new HashMap<>();
    private final Map<UUID, PlayerStorage> PLAYER_STORAGE = new HashMap<>();
    private final WolfyCoreCommon core;

    public PersistentStorage(WolfyCoreCommon core) {
        this.core = core;
    }

    /**
     * Gets the already existing storage instance of that world or creates a new one.
     *
     * @param world The world to get/create the storage for.
     * @return The world storage instance of the specified world.
     */
    public WorldStorage getOrCreateWorldStorage(@NotNull World world) {
        Preconditions.checkNotNull(world, "The world cannot be null!");
        return WORLD_STORAGE.computeIfAbsent(world.getUID(), uuid -> new WorldStorage(core, uuid));
    }

    /**
     * Gets the existing storage instance of the given player, or creates a new one when it doesn't exist.
     *
     * @param player The player to get the data for.
     * @return The player storage instance of the specified player.
     */
    public PlayerStorage getOrCreatePlayerStorage(@NotNull Player player) {
        Preconditions.checkNotNull(player, "The player cannot be null!");
        return PLAYER_STORAGE.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerStorage(core, uuid));
    }

    public WolfyCoreCommon getCore() {
        return core;
    }
}
