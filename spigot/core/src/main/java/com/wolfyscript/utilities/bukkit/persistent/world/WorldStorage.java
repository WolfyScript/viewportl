package com.wolfyscript.utilities.bukkit.persistent.world;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.math.Vec2i;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.util.Vector;

public class WorldStorage {

    private final Map<Vec2i, ChunkStorage> CHUNK_DATA = new HashMap<>();

    private final WolfyCoreCommon core;
    private final UUID worldUUID;

    public WorldStorage(WolfyCoreCommon core, UUID world) {
        this.worldUUID = world;
        this.core = core;
    }

    public WolfyCoreCommon getCore() {
        return core;
    }

    protected Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldUUID));
    }

    public ChunkStorage getOrCreateChunkStorage(Vec2i chunkCoords) {
        return CHUNK_DATA.computeIfAbsent(chunkCoords, vec2i -> ChunkStorage.create(this, vec2i));
    }

    public ChunkStorage getOrCreateChunkStorage(int chunkX, int chunkZ) {
        return getOrCreateChunkStorage(new Vec2i(chunkX, chunkZ));
    }

    public ChunkStorage getOrCreateChunkStorage(Location location) {
        return getOrCreateChunkStorage(new Vec2i(location.getBlockX() >> 4, location.getBlockZ() >> 4));
    }

    private ChunkStorage getOrCreateChunkStorage(Vector pos) {
        return getOrCreateChunkStorage(new Vec2i(pos.getBlockX() >> 4, pos.getBlockZ() >> 4));
    }

    public BlockStorage getOrCreateAndSetBlockStorage(Location location) {
        return getOrCreateChunkStorage(location).getOrCreateAndSetBlockStorage(location);
    }

    /**
     * Gets the BlockStorage if it exists; otherwise creates a new instance via {@link #createBlockStorage(Location)}
     *
     * @param location The location of the block.
     * @return The BlockStorage of the block if it exists; otherwise a new BlockStorage instance for the block.
     */
    public BlockStorage getOrCreateBlockStorage(Location location) {
        return getOrCreateChunkStorage(location).getOrCreateBlockStorage(location);
    }

    /**
     * Creates a new BlockStorage for the specified location.<br>
     * The BlockStorage can then be applied to the block using {@link #setBlockStorageIfAbsent(BlockStorage)}
     *
     * @param location The location of the block.
     * @return The new instance of the BlockStorage.
     */
    public BlockStorage createBlockStorage(Location location) {
        return getOrCreateChunkStorage(location).createBlockStorage(location);
    }

    /**
     * Applies the specified BlockStorage to the ChunkStorage if it isn't occupied by another storage yet.
     *
     * @param blockStorage The BlockStorage to apply.
     */
    public void setBlockStorageIfAbsent(BlockStorage blockStorage) {
        getOrCreateChunkStorage(blockStorage.getPos()).setBlockStorageIfAbsent(blockStorage);
    }

    public void unloadChunk(ChunkStorage chunkStorage) {
        chunkStorage.getChunk().ifPresent(chunk -> CHUNK_DATA.remove(new Vec2i(chunk.getX(), chunk.getZ())));
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public Optional<BlockStorage> removeBlock(Location location) {
        return getOrCreateChunkStorage(location).removeBlock(location);
    }

    public boolean isBlockStored(Location location) {
        return getBlock(location).isPresent();
    }

    public Optional<BlockStorage> getBlock(Location location) {
        return getOrCreateChunkStorage(location).getBlock(location);
    }

    protected Optional<PersistentDataContainer> getWorldContainer() {
        return getWorld().map(PersistentDataHolder::getPersistentDataContainer);
    }

}
