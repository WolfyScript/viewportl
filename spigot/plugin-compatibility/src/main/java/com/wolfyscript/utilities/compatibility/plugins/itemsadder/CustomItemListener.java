package com.wolfyscript.utilities.compatibility.plugins.itemsadder;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageBreakEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoragePlaceEvent;
import com.wolfyscript.utilities.bukkit.listeners.PersistentStorageListener;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;

public class CustomItemListener implements Listener {

    private final ItemsAdderImpl iaImpl;
    private final WolfyCoreCommon core;

    public CustomItemListener(ItemsAdderImpl iaImpl) {
        this.iaImpl = iaImpl;
        this.core = (WolfyCoreCommon) WolfyCoreSpigot.getInstance();
    }

    @EventHandler
    public void onBlockPlacement(CustomBlockPlaceEvent event) {
        /*
        Call the StoragePlaceEvent when placing IA blocks!
         */
        if (event.isCanBuild()) {
            Block block = event.getBlock();
            WorldStorage worldStorage = ((WolfyCoreCommon) WolfyCore.getInstance()).persistentStorage.getOrCreateWorldStorage(block.getWorld());
            BlockStorage blockStorage = worldStorage.createBlockStorage(block.getLocation());
            var blockStorePlaceEvent = new BlockStoragePlaceEvent(block, blockStorage, event.getReplacedBlockState(), event.getPlacedAgainst(), event.getItemInHand(), event.getPlayer(), event.isCanBuild(),
                    // Since we do not get the hand used, we need to guess it.
                    event.getItemInHand().isSimilar(event.getPlayer().getEquipment().getItemInMainHand()) ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND
            );
            blockStorePlaceEvent.setCancelled(event.isCancelled());
            Bukkit.getPluginManager().callEvent(blockStorePlaceEvent);
            event.setCancelled(blockStorePlaceEvent.isCancelled());

            if (!blockStorage.isEmpty() && !blockStorePlaceEvent.isCancelled()) {
                worldStorage.setBlockStorageIfAbsent(blockStorage);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(CustomBlockBreakEvent event) {
        /*
        Doesn't look like this is actually required. Include it anyway just in case as it is just called if the stored data still exists!
         */
        var block = event.getBlock();
        var worldStorage = ((WolfyCoreCommon) WolfyCore.getInstance()).persistentStorage.getOrCreateWorldStorage(block.getWorld());
        worldStorage.getBlock(block.getLocation()).ifPresent(store -> {
            if (!store.isEmpty()) {
                var blockStorageBreakEvent = new BlockStorageBreakEvent(event.getBlock(), store, event.getPlayer());
                blockStorageBreakEvent.setCancelled(event.isCancelled());
                Bukkit.getPluginManager().callEvent(blockStorageBreakEvent);
                if (blockStorageBreakEvent.isCancelled()) return;
                worldStorage.removeBlock(block.getLocation()).ifPresent(storage -> {
                    event.getBlock().setMetadata(PersistentStorageListener.PREVIOUS_BROKEN_STORE, new FixedMetadataValue(core.getWolfyUtils().getPlugin(), storage));
                });
            }
        });
    }

}
