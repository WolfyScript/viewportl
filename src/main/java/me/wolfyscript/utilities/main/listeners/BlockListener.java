package me.wolfyscript.utilities.main.listeners;

import com.sk89q.worldedit.world.item.ItemType;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItemBreakEvent;
import me.wolfyscript.utilities.api.custom_items.CustomItemPlaceEvent;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
            if (storedItem != null) {
                event.setDropItems(false);
                CustomItemBreakEvent event1 = new CustomItemBreakEvent(storedItem, event);
                Bukkit.getPluginManager().callEvent(event1);
                event.setCancelled(event1.isCancelled());
                event.setDropItems(event1.isDropItems());
                storedItem = event1.getCustomItem();
                if (!event1.isCancelled()) {
                    if (storedItem != null) {
                        block.getWorld().dropItemNaturally(block.getLocation(), storedItem);
                        CustomItems.removeStoredBlockItem(block.getLocation());
                        if (block.getBlockData() instanceof Bisected) {
                            if (((Bisected) block.getBlockData()).getHalf().equals(Bisected.Half.BOTTOM)) {
                                CustomItems.removeStoredBlockItem(block.getLocation().add(0, 1, 0));
                            } else {
                                CustomItems.removeStoredBlockItem(block.getLocation().subtract(0, 1, 0));
                            }
                        }else if(block.getBlockData() instanceof Bed){
                            Bed bed = (Bed) block.getBlockData();
                            CustomItems.removeStoredBlockItem(block.getLocation().add(bed.getFacing().getDirection()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!event.isCancelled()) {
            if(event.getNewState().getType().equals(Material.AIR)){
                Block block = event.getBlock();
                CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
                if (storedItem != null) {
                    CustomItems.removeStoredBlockItem(block.getLocation());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            String customItemID = getCustomItemID(event.getItemInHand());
            if (customItemID != null && !customItemID.isEmpty()) {
                CustomItem customItem = CustomItems.getCustomItem(customItemID);
                CustomItemPlaceEvent event1 = new CustomItemPlaceEvent(customItem, event);
                Bukkit.getPluginManager().callEvent(event1);
                customItem = event1.getCustomItem();
                if (!event1.isCancelled()) {
                    if (customItem != null) {
                        CustomItems.setStoredBlockItem(event.getBlockPlaced().getLocation(), customItem);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    private String getCustomItemID(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            if (itemMeta.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING)) {
                return itemMeta.getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING);
            }
        } else {
            if (ItemUtils.isInItemSettings(itemMeta, "custom_item")) {
                return (String) ItemUtils.getFromItemSettings(itemMeta, "custom_item");
            }
        }
        return "";
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlaceMulti(BlockMultiPlaceEvent event) {
        if (!event.isCancelled()) {
            String customItemID = getCustomItemID(event.getItemInHand());
            if (customItemID != null && !customItemID.isEmpty()) {
                CustomItem customItem = CustomItems.getCustomItem(customItemID);
                for (BlockState state : event.getReplacedBlockStates()) {
                    CustomItems.setStoredBlockItem(state.getLocation(), customItem);
                }
            }
        }
    }
}
