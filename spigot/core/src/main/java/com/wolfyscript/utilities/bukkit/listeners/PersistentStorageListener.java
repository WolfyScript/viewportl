package com.wolfyscript.utilities.bukkit.listeners;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageBreakEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageDropItemsEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageMultiPlaceEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoragePlaceEvent;
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PersistentStorageListener implements Listener {

    public static final String PREVIOUS_BROKEN_STORE = "previous_store";

    private final WolfyCoreCommon core;
    private final PersistentStorage persistentStorage;

    public PersistentStorageListener(WolfyCoreCommon core) {
        this.core = core;
        this.persistentStorage = core.persistentStorage;
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        PlayerStorage playerStorage = persistentStorage.getOrCreatePlayerStorage(event.getPlayer());
        playerStorage.updateAndClearCache(); // Clear cache when player leaves the server, to not waste memory!
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        initOrUpdateChunk(chunk);
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        WorldStorage worldStorage = persistentStorage.getOrCreateWorldStorage(event.getWorld());
        ChunkStorage chunkStorage = worldStorage.getOrCreateChunkStorage(chunk.getX(), chunk.getZ());
        chunkStorage.getStoredBlocks().forEach((vector, store) -> {
            store.onUnload();
        });
        worldStorage.unloadChunk(chunkStorage);
    }

    @EventHandler
    private void onSave(WorldSaveEvent event) {
        World world = event.getWorld();
    }

    /**
     * This is required since the world was loaded before the plugin was enabled, therefor the spawn-chunks are already loaded.
     * So this makes sure to initialize the existing chunks.
     */
    @EventHandler
    private void onServerLoad(ServerLoadEvent event) {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                initOrUpdateChunk(chunk);
            }
        }
    }

    private void initOrUpdateChunk(Chunk chunk) {
        ChunkStorage chunkStorage = persistentStorage.getOrCreateWorldStorage(chunk.getWorld()).getOrCreateChunkStorage(chunk.getX(), chunk.getZ());
        chunkStorage.loadBlocksIntoCache();
        chunkStorage.getStoredBlocks().forEach((vector, blockStorage) -> blockStorage.onLoad());
    }

    /* ******************** *
     * Block Storage events *
     * ******************** */

    /**
     * Handles the BlockStorages that are placed together with blocks.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockStoragePlace(BlockPlaceEvent event) {
        if (event.canBuild()) {
            Block block = event.getBlock();
            WorldStorage worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
            BlockStorage blockStorage = worldStorage.createBlockStorage(block.getLocation());
            var blockStorePlaceEvent = new BlockStoragePlaceEvent(block, blockStorage, event.getBlockReplacedState(), event.getBlockAgainst(), event.getItemInHand(), event.getPlayer(), event.canBuild(), event.getHand());
            blockStorePlaceEvent.setCancelled(event.isCancelled());
            Bukkit.getPluginManager().callEvent(blockStorePlaceEvent);
            event.setCancelled(blockStorePlaceEvent.isCancelled());

            if (!blockStorage.isEmpty() && !blockStorePlaceEvent.isCancelled()) {
                worldStorage.setBlockStorageIfAbsent(blockStorage);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceMulti(BlockMultiPlaceEvent event) {
        WorldStorage worldStorage = persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld());
        List<BlockStorage> storages = event.getReplacedBlockStates().stream().map(state -> worldStorage.createBlockStorage(state.getLocation())).toList();
        var blockStorageMultiPlaceEvent = new BlockStorageMultiPlaceEvent(event.getReplacedBlockStates(), storages, event.getBlockAgainst(), event.getItemInHand(), event.getPlayer(), event.canBuild(), event.getHand());
        blockStorageMultiPlaceEvent.setCancelled(event.isCancelled());
        Bukkit.getPluginManager().callEvent(blockStorageMultiPlaceEvent);
        event.setCancelled(blockStorageMultiPlaceEvent.isCancelled());

        if (blockStorageMultiPlaceEvent.isCancelled()) return;

        for (int i = 0; i < blockStorageMultiPlaceEvent.getBlockStorages().size(); i++) {
            BlockStorage blockStorage = blockStorageMultiPlaceEvent.getBlockStorages().get(i);
            if (!blockStorage.isEmpty()) {
                worldStorage.setBlockStorageIfAbsent(blockStorage);
            }
        }
    }

    /**
     * Called when liquid flows or when a Dragon Egg teleports.
     * This Listener only listens for the Dragon Egg.
     * The BlockStorage is copied from the original position to the new postion.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        var block = event.getBlock();
        var worldStore = persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld());
        worldStore.getBlock(block.getLocation()).ifPresent(store -> {
            Location loc = event.getToBlock().getLocation();
            worldStore.removeBlock(block.getLocation());
            store.copyToOtherBlockStorage(worldStore.getOrCreateAndSetBlockStorage(loc));
        });
    }

    /**
     * Makes sure that the positions of BlockStorages are updated correctly when pushed by a piston.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        updatePistonBlocks(event.getBlock().getWorld(), event.getBlocks(), event.getDirection());
    }

    /**
     * Makes sure that the positions of BlockStorages are updated correctly when pulled by a piston.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        updatePistonBlocks(event.getBlock().getWorld(), event.getBlocks(), event.getDirection());
    }

    private void updatePistonBlocks(World world, List<Block> blocks, BlockFace direction) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(world);
        blocks.forEach(block -> worldStorage.getBlock(block.getLocation()).ifPresent(store -> {
            Location moveTo = block.getRelative(direction).getLocation();
            worldStorage.removeBlock(block.getLocation());
            store.copyToOtherBlockStorage(worldStorage.getOrCreateAndSetBlockStorage(moveTo));
        }));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockStorageBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        worldStorage.getBlock(block.getLocation()).ifPresent(store -> {
            if (!store.isEmpty()) {
                var blockStorageBreakEvent = new BlockStorageBreakEvent(event.getBlock(), store, event.getPlayer());
                blockStorageBreakEvent.setCancelled(event.isCancelled());
                Bukkit.getPluginManager().callEvent(blockStorageBreakEvent);
                if (blockStorageBreakEvent.isCancelled()) return;
                worldStorage.removeBlock(block.getLocation()).ifPresent(storage -> {
                    if (event.isDropItems()) {
                        event.getBlock().setMetadata(PREVIOUS_BROKEN_STORE, new FixedMetadataValue(core.getWolfyUtils().getPlugin(), storage));
                    }
                });
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplodeBlockStorages(EntityExplodeEvent event) {
        event.setCancelled(handleExplodedBlockStorages(persistentStorage.getOrCreateWorldStorage(event.getEntity().getWorld()), event.blockList()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplodeStorages(BlockExplodeEvent event) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld());
        worldStorage.removeBlock(event.getBlock().getLocation()); // Remove the block that exploded, since that might have had custom data.
        //event.setYield(0f);
        event.setCancelled(handleExplodedBlockStorages(worldStorage, event.blockList()));
    }

    private boolean handleExplodedBlockStorages(WorldStorage worldStorage, List<Block> blocks) {
        boolean cancel = blocks.stream().anyMatch(block -> worldStorage.removeBlock(block.getLocation()).isPresent());
        if (cancel) {
            // Only use the custom behaviour if a block storage was included in the explosion
            Iterator<Block> blockIterator = blocks.iterator();
            while (blockIterator.hasNext()) {
                Block block = blockIterator.next();
                Location location = block.getLocation();
                blockIterator.remove();
                Collection<ItemStack> itemStacks = block.getDrops();
                BlockState state = block.getState();
                block.setType(Material.AIR);
                // Handle custom block storage drops, if available
                worldStorage.getOrCreateChunkStorage(location).getBlock(location).ifPresentOrElse(storage -> {
                    World world = location.getWorld();
                    if (world != null) {
                        List<Item> itemDrops = itemStacks.stream().map(itemStack -> world.dropItemNaturally(location, itemStack)).toList();
                        // Call the drop event for plugins to manipulate the drops
                        var blockStoreDropItemsEvent = new BlockStorageDropItemsEvent(block, state, storage, null, new ArrayList<>(itemDrops));
                        Bukkit.getPluginManager().callEvent(blockStoreDropItemsEvent);
                        List<Item> eventItems = blockStoreDropItemsEvent.getItems();
                        if (blockStoreDropItemsEvent.isCancelled()) {
                            blockStoreDropItemsEvent.getItems().clear();
                        }
                        //Remove the items that were removed from the list
                        itemDrops.stream().filter(item -> !eventItems.contains(item)).forEach(Entity::remove);
                    }
                }, () -> {
                    // Otherwise just drop the items
                    World world = location.getWorld();
                    if (world != null) {
                        itemStacks.forEach(itemStack -> world.dropItemNaturally(location, itemStack));
                    }
                });
            }
            return true;
        }
        return false;
    }

    private void removeMultiBlockItems(Block block) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        if (block.getBlockData() instanceof Bisected bisected) {
            worldStorage.removeBlock(bisected.getHalf().equals(Bisected.Half.BOTTOM) ? block.getLocation().add(0, 1, 0) : block.getLocation().subtract(0, 1, 0));
        } else if (block.getBlockData() instanceof Bed bed) {
            worldStorage.removeBlock(block.getLocation().add(bed.getFacing().getDirection()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockStorageItemDrop(BlockDropItemEvent event) {
        var state = event.getBlockState(); // Get previous state with old metadata.
        state.getMetadata(PREVIOUS_BROKEN_STORE).stream().filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), core)).findFirst().ifPresent(metadataValue -> {
            event.getBlock().removeMetadata(PREVIOUS_BROKEN_STORE, core.getWolfyUtils().getPlugin()); //Remove old metadata from block!
            if (metadataValue.value() instanceof BlockStorage store) {
                var blockStoreDropItemsEvent = new BlockStorageDropItemsEvent(event.getBlock(), state, store, event.getPlayer(), event.getItems());
                Bukkit.getPluginManager().callEvent(blockStoreDropItemsEvent);
                event.setCancelled(blockStoreDropItemsEvent.isCancelled());
            }
        });
    }

    /**
     * Removes the BlockStorage if the block is burned.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent event) {
        removeIfAvailable(event.getBlock());
    }

    /**
     * Removes the BlockStorage if the block is a leave and decays.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent event) {
        removeIfAvailable(event.getBlock());
    }

    /**
     * Removes the BlockStorage if the block disappears because of world conditions.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getNewState().getType().equals(Material.AIR)) {
            removeIfAvailable(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getChangedType().equals(Material.AIR)) {
            removeIfAvailable(event.getBlock());
        }
    }

    private void removeIfAvailable(Block block) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        worldStorage.getBlock(block.getLocation()).ifPresent(customItemStore -> {
            worldStorage.removeBlock(block.getLocation());
        });
    }


}
