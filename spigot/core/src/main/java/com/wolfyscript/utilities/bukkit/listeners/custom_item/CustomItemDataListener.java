package com.wolfyscript.utilities.bukkit.listeners.custom_item;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.events.CustomItemPlaceEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageBreakEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageDropItemsEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageMultiPlaceEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoragePlaceEvent;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.bukkit.world.items.CustomItemBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class CustomItemDataListener implements Listener {

    private final WolfyCoreCommon core;

    public CustomItemDataListener(WolfyCoreCommon core) {
        this.core = core;
    }

    @EventHandler
    public void onDropItems(BlockStorageDropItemsEvent event) {
        event.getStorage().getData(CustomItemBlockData.ID, CustomItemBlockData.class).ifPresent(data -> {
            var blockState = event.getBlockState();
            data.getCustomItem().ifPresent(customItem -> {
                if (customItem.getBlockSettings().isUseCustomDrops()) {
                    event.setCancelled(true);
                    //TODO for future: Let people customize this!
                    ItemStack result = customItem.create();
                    if (blockState instanceof Container container) {
                        var blockStateMeta = (BlockStateMeta) result.getItemMeta();
                        if (container instanceof ShulkerBox) {
                            var shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                            shulkerBox.getInventory().setContents(container.getInventory().getContents());
                            blockStateMeta.setBlockState(shulkerBox);
                        } else {
                            var itemContainer = (Container) blockStateMeta.getBlockState();
                            itemContainer.getInventory().clear();
                            blockStateMeta.setBlockState(itemContainer);
                        }
                        result.setItemMeta(blockStateMeta);
                    }
                    blockState.getWorld().dropItemNaturally(blockState.getLocation(), result);
                }
            });
        });
    }

    @EventHandler
    public void onPlaceBlock(BlockStoragePlaceEvent event) {
        var customItem = CustomItem.getByItemStack(event.getItemInHand());
        if (!ItemUtils.isAirOrNull(customItem) && customItem.isBlock()) {
            if (customItem.isBlockPlacement()) {
                event.setCancelled(true);
            }
            var event1 = new CustomItemPlaceEvent(customItem, event);
            Bukkit.getPluginManager().callEvent(event1);
            customItem = event1.getCustomItem();
            if (!event1.isCancelled()) {
                if (customItem != null) {
                    Location blockLoc = event.getBlockPlaced().getLocation();
                    ChunkStorage chunkStorage = core.persistentStorage.getOrCreateWorldStorage(blockLoc.getWorld()).getOrCreateChunkStorage(blockLoc);
                    var customItemData = new CustomItemBlockData(core, chunkStorage, blockLoc.toVector(), customItem.key());
                    event.getStorage().addOrSetData(customItemData);
                    customItemData.onPlace(event);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMultiPlaceBlock(BlockStorageMultiPlaceEvent event) {
        var customItem = CustomItem.getByItemStack(event.getItemInHand());
        if (!ItemUtils.isAirOrNull(customItem)) {
            if (customItem.isBlockPlacement()) {
                event.setCancelled(true);
                return;
            }
            event.getBlockStorages().forEach(blockStorage -> {
                Location blockLoc = event.getBlockPlaced().getLocation();
                ChunkStorage chunkStorage = core.persistentStorage.getOrCreateWorldStorage(blockLoc.getWorld()).getOrCreateChunkStorage(blockLoc);
                var customItemData = new CustomItemBlockData(core, chunkStorage, blockLoc.toVector(), customItem.key());
                blockStorage.addOrSetData(customItemData);
                customItemData.onPlace(event);
            });
        }
    }

    @EventHandler
    public void onBreakBlock(BlockStorageBreakEvent event) {
        event.getStorage().getData(CustomItemBlockData.ID, CustomItemBlockData.class).ifPresent(data -> {
            data.onBreak(event);
        });
    }


}
