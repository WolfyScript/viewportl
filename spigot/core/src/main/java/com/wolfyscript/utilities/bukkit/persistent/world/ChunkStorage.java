package com.wolfyscript.utilities.bukkit.persistent.world;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.math.Vec2i;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

@JsonIncludeProperties
public class ChunkStorage {

    public static final NamespacedKey BLOCKS_KEY = new NamespacedKey("wolfyutils", "blocks");

    private static final String BLOCK_POS_KEY = "%s_%s_%s"; //x, y, z
    private static final String BLOCK_POS_NAMESPACE = "wolfyutils"; //-> "wolfyutils:x_y_z"

    private final Map<Vector, BlockStorage> BLOCKS = new HashMap<>();

    private final Vec2i coords;
    private final WorldStorage worldStorage;
    private final WolfyCoreCommon core;

    private ChunkStorage(WorldStorage worldStorage, Vec2i coords) {
        this.coords = coords;
        this.worldStorage = worldStorage;
        this.core = worldStorage.getCore();
    }

    public WolfyCoreCommon getCore() {
        return core;
    }

    /**
     * Gets the parent WorldStorage of this ChunkStorage.
     *
     * @return The parent WorldStorage.
     */
    public WorldStorage getWorldStorage() {
        return worldStorage;
    }

    /**
     * Creates a new ChunkStorage for the specified chunk coords and WorldStorage.
     *
     * @param worldStorage The parent WorldStorage.
     * @param coords       The chunk coords.
     * @return The newly created ChunkStorage instance.
     */
    public static ChunkStorage create(WorldStorage worldStorage, Vec2i coords) {
        return new ChunkStorage(worldStorage, coords);
    }

    /**
     * Loads the blocks from the PersistentDataContainer into the cache.<br>
     * From this point on the cache and PersistentDataContainer is kept in sync whenever adding/removing blocks.<br>
     * <br>
     * <b>If for whatever reason the PersistentDataContainer was modified, this method should be called to update the cache!</b>
     */
    public void loadBlocksIntoCache() {
        getPersistentBlocksContainer().ifPresent(blocks -> {
            blocks.getKeys().forEach(key -> {
                String[] coordsStrings = key.getKey().split("_");
                int[] coords = new int[3];
                for (int i = 0; i < coordsStrings.length; i++) {
                    coords[i] = Integer.parseInt(coordsStrings[i]);
                }
                var coordsVec = new Vector(coords[0], coords[1], coords[2]);
                setBlockStorageIfAbsent(blocks.get(key, new BlockStorage.PersistentType(this, coordsVec)));
            });
        });
    }

    /**
     * Gets the Chunk this Storage belongs to.<br>
     * <b>This may load the chunk if it isn't already!</b>
     *
     * @return The chunk this storage belongs to.
     */
    public Optional<Chunk> getChunk() {
        return worldStorage.getWorld().map(world -> world.getChunkAt(coords.getX(), coords.getY()));
    }

    /**
     * Gets the PersistentDataContainer of the Chunk this Storage belongs to.<br>
     * <b>This may load the chunk if it isn't already!</b>
     *
     * @return The chunk this storage belongs to.
     */
    protected Optional<PersistentDataContainer> getPersistentContainer() {
        return worldStorage.getWorld().map(world -> world.getChunkAt(coords.getX(), coords.getY()).getPersistentDataContainer());
    }

    private Optional<PersistentDataContainer> getPersistentBlocksContainer() {
        return getPersistentContainer().map(container -> {
            var context = container.getAdapterContext();
            if (!container.has(BLOCKS_KEY, PersistentDataType.TAG_CONTAINER)) {
                container.set(BLOCKS_KEY, PersistentDataType.TAG_CONTAINER, context.newPersistentDataContainer());
            }
            return container.get(BLOCKS_KEY, PersistentDataType.TAG_CONTAINER);
        });
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.<br>
     * <i>This converts the location to a Vector and uses {@link #removeBlock(Vector)}</i>
     *
     * @param location The target location of the block
     * @return Optional of the previously stored data; otherwise empty Optional.
     * @see #removeBlock(Vector)
     */
    public Optional<BlockStorage> removeBlock(Location location) {
        return removeBlock(location.toVector());
    }

    /**
     * Removes the BlockStorage at the specified position.
     *
     * @param pos The position vector of the BlockStorage
     * @return Optional of the previously stored data; otherwise empty Optional.
     * @see #removeBlock(Location)
     */
    public Optional<BlockStorage> removeBlock(Vector pos) {
        var previousStore = BLOCKS.remove(pos);
        updateBlock(pos);
        if (previousStore != null) {
            previousStore.onUnload();
            return Optional.of(previousStore);
        }
        return Optional.empty();
    }

    /**
     * Gets the BlockStorage if it exists; otherwise creates a new instance via {@link #createBlockStorage(Location)}.<br>
     *
     * In case the BlockStorage doesn't exist yet, then the new BlockStorage instance is directly applied to the ChunkStorage.<br>
     *
     * If that is not required or desired, then {@link #getOrCreateBlockStorage(Location)} and {@link #setBlockStorageIfAbsent(BlockStorage)} provide the same functionality together.
     *
     * @param location The location of the Block.
     * @return The existing BlockStorage; otherwise a new BlockStorage Instance.
     */
    public BlockStorage getOrCreateAndSetBlockStorage(Location location) {
        var pos = location.toVector();
        BlockStorage blockStorage = BLOCKS.computeIfAbsent(pos, vector -> createBlockStorage(location));
        updateBlock(blockStorage.getPos());
        return blockStorage;
    }

    /**
     * Gets the BlockStorage if it exists; otherwise creates a new instance via {@link #createBlockStorage(Location)}
     *
     * @param location The location of the block.
     * @return The BlockStorage of the block if it exists; otherwise a new BlockStorage instance for the block.
     */
    public BlockStorage getOrCreateBlockStorage(Location location) {
        var pos = location.toVector();
        return BLOCKS.getOrDefault(pos, createBlockStorage(location));
    }

    /**
     * Creates a new BlockStorage for the specified location.<br>
     * The BlockStorage can then be applied to the block using {@link #setBlockStorageIfAbsent(BlockStorage)}
     *
     * @param location The location of the block.
     * @return The new instance of the BlockStorage.
     */
    public BlockStorage createBlockStorage(Location location) {
        var pos = location.toVector();
        var persistentBlockContainer = getPersistentContainer().map(container -> container.getAdapterContext().newPersistentDataContainer()).orElseThrow(() -> new RuntimeException("Failed to create PersistentDataContainer!"));
        return new BlockStorage(this, pos, persistentBlockContainer);
    }

    /**
     * Applies the specified BlockStorage to the ChunkStorage if it isn't occupied by another storage yet.
     *
     * @param blockStorage The BlockStorage to apply.
     */
    public void setBlockStorageIfAbsent(BlockStorage blockStorage) {
        BLOCKS.putIfAbsent(blockStorage.getPos(), blockStorage);
        updateBlock(blockStorage.getPos());
    }

    /**
     * Checks if there is an existing BlockStorage at the specified Location.
     *
     * @param location The location to check for a BlockStorage.
     * @return True if there exists a BlockStorage at the location; otherwise false.
     */
    public boolean isBlockStored(Location location) {
        return BLOCKS.containsKey(location.toVector());
    }

    /**
     * Gets the stored block at the specified location.
     *
     * @param location The location of the block.
     * @return The stored block if stored; otherwise empty Optional.
     */
    public Optional<BlockStorage> getBlock(Location location) {
        return Optional.ofNullable(BLOCKS.get(location.toVector()));
    }

    /**
     * Gets the stored blocks in the chunk.
     *
     * @return The stored blocks in the chunk.
     */
    public Map<Vector, BlockStorage> getStoredBlocks() {
        return BLOCKS.entrySet().stream().filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Updates the specified block position in the PersistentStorageContainer.
     *
     * @param blockPos The block position to update.
     */
    public void updateBlock(Vector blockPos) {
        getPersistentBlocksContainer().ifPresent(blocks -> {
            var value = BLOCKS.get(blockPos);
            var key = createKeyForBlock(blockPos);
            if (value != null && !value.isEmpty()) { //Do not store empty storage in NBT, but keep them in cache.
                blocks.set(key, new BlockStorage.PersistentType(this, blockPos), value);
            } else {
                blocks.remove(key);
            }
            getPersistentContainer().ifPresent(container -> container.set(BLOCKS_KEY, PersistentDataType.TAG_CONTAINER, blocks));
        });
    }

    /**
     * Creates a new key for the specified block position.<br>
     * Format of key: "wolfyutils:&lt;+/-x&gt;_&lt;+/-y&gt;_&lt;+/-z&gt;"<br>
     * The key can have a maximum of 255 characters.<br>
     * "wolfyutils" + ":" + "_"*2 = 13 -> leaves space for 242 characters for x, y, and z including +/-.
     *
     * @param blockPos
     * @return
     */
    private NamespacedKey createKeyForBlock(Vector blockPos) {
        return new NamespacedKey(BLOCK_POS_NAMESPACE, BLOCK_POS_KEY.formatted(blockPos.getBlockX(), blockPos.getBlockY(), blockPos.getBlockZ()));
    }

}
