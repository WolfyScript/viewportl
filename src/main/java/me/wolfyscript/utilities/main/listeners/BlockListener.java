package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItemBreakEvent;
import me.wolfyscript.utilities.api.custom_items.CustomItemPlaceEvent;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
                        ItemStack result = storedItem.create();
                        CustomItems.removeStoredBlockItem(block.getLocation());
                        if (block.getState() instanceof Container) {
                            Container container = (Container) block.getState();
                            BlockStateMeta blockStateMeta = (BlockStateMeta) result.getItemMeta();
                            if (container instanceof ShulkerBox) {
                                ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                                shulkerBox.getInventory().setContents(container.getInventory().getContents());
                                blockStateMeta.setBlockState(shulkerBox);
                            } else {
                                Container itemContainer = (Container) blockStateMeta.getBlockState();
                                itemContainer.getInventory().clear();
                                blockStateMeta.setBlockState(itemContainer);
                            }
                            result.setItemMeta(blockStateMeta);
                        }
                        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                            block.getWorld().dropItemNaturally(block.getLocation(), result);
                        }
                        if (block.getBlockData() instanceof Bisected) {
                            if (((Bisected) block.getBlockData()).getHalf().equals(Bisected.Half.BOTTOM)) {
                                CustomItems.removeStoredBlockItem(block.getLocation().add(0, 1, 0));
                            } else {
                                CustomItems.removeStoredBlockItem(block.getLocation().subtract(0, 1, 0));
                            }
                        } else if (block.getBlockData() instanceof Bed) {
                            Bed bed = (Bed) block.getBlockData();
                            CustomItems.removeStoredBlockItem(block.getLocation().add(bed.getFacing().getDirection()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!event.isCancelled()) {
            Iterator<Block> blockList = event.blockList().iterator();
            while (blockList.hasNext()) {
                Block block = blockList.next();
                CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
                if (storedItem != null) {
                    blockList.remove();
                    ItemStack result = storedItem.create();
                    CustomItems.removeStoredBlockItem(block.getLocation());
                    if (block.getState() instanceof Container) {
                        Container container = (Container) block.getState();
                        BlockStateMeta blockStateMeta = (BlockStateMeta) result.getItemMeta();
                        if (container instanceof ShulkerBox) {
                            ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                            shulkerBox.getInventory().setContents(container.getInventory().getContents());
                            blockStateMeta.setBlockState(shulkerBox);
                        } else {
                            Container itemContainer = (Container) blockStateMeta.getBlockState();
                            itemContainer.getInventory().clear();
                            blockStateMeta.setBlockState(itemContainer);
                        }
                        result.setItemMeta(blockStateMeta);
                    }
                    block.setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getLocation(), result);
                    if (block.getBlockData() instanceof Bisected) {
                        if (((Bisected) block.getBlockData()).getHalf().equals(Bisected.Half.BOTTOM)) {
                            CustomItems.removeStoredBlockItem(block.getLocation().add(0, 1, 0));
                        } else {
                            CustomItems.removeStoredBlockItem(block.getLocation().subtract(0, 1, 0));
                        }
                    } else if (block.getBlockData() instanceof Bed) {
                        Bed bed = (Bed) block.getBlockData();
                        CustomItems.removeStoredBlockItem(block.getLocation().add(bed.getFacing().getDirection()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!event.isCancelled()) {
            Iterator<Block> blockList = event.blockList().iterator();
            while (blockList.hasNext()) {
                Block block = blockList.next();
                CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
                if (storedItem != null) {
                    blockList.remove();
                    ItemStack result = storedItem.create();
                    CustomItems.removeStoredBlockItem(block.getLocation());
                    if (block.getState() instanceof Container) {
                        Container container = (Container) block.getState();
                        BlockStateMeta blockStateMeta = (BlockStateMeta) result.getItemMeta();
                        if (container instanceof ShulkerBox) {
                            ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                            shulkerBox.getInventory().setContents(container.getInventory().getContents());
                            blockStateMeta.setBlockState(shulkerBox);
                        } else {
                            Container itemContainer = (Container) blockStateMeta.getBlockState();
                            itemContainer.getInventory().clear();
                            blockStateMeta.setBlockState(itemContainer);
                        }
                        result.setItemMeta(blockStateMeta);
                    }
                    block.setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getLocation(), result);
                    if (block.getBlockData() instanceof Bisected) {
                        if (((Bisected) block.getBlockData()).getHalf().equals(Bisected.Half.BOTTOM)) {
                            CustomItems.removeStoredBlockItem(block.getLocation().add(0, 1, 0));
                        } else {
                            CustomItems.removeStoredBlockItem(block.getLocation().subtract(0, 1, 0));
                        }
                    } else if (block.getBlockData() instanceof Bed) {
                        Bed bed = (Bed) block.getBlockData();
                        CustomItems.removeStoredBlockItem(block.getLocation().add(bed.getFacing().getDirection()));
                    }
                }
            }
        }
    }

    /*
    Called when liquid flows or when an Dragon Egg teleports.
    This Listener only listens for the Dragon Egg
    */
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
            if (storedItem != null) {
                CustomItems.removeStoredBlockItem(block.getLocation());
                CustomItems.setStoredBlockItem(event.getToBlock().getLocation(), storedItem);
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!event.isCancelled()) {
            List<Block> blocks = event.getBlocks();
            HashMap<Location, CustomItem> newLocations = new HashMap<>();
            for (Block block : blocks) {
                CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
                if (storedItem != null) {
                    CustomItems.removeStoredBlockItem(block.getLocation());
                    newLocations.put(block.getRelative(event.getDirection()).getLocation(), storedItem);
                }
            }
            newLocations.forEach((location, customItem) -> CustomItems.setStoredBlockItem(location, customItem));
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isCancelled() && event.isSticky()) {
            List<Block> blocks = event.getBlocks();
            HashMap<Location, CustomItem> newLocations = new HashMap<>();
            for (Block block : blocks) {
                CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
                if (storedItem != null) {
                    CustomItems.removeStoredBlockItem(block.getLocation());
                    newLocations.put(block.getRelative(event.getDirection()).getLocation(), storedItem);
                }
            }
            newLocations.forEach((location, customItem) -> CustomItems.setStoredBlockItem(location, customItem));
        }
    }

    /*
    Unregisters the placed CustomItem when the block is burned by fire.
     */
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
            if (storedItem != null) {
                CustomItems.removeStoredBlockItem(block.getLocation());
            }
        }
    }

    /*
    Unregisters the placed CustomItem when the CustomItem is a leaf and decays.
     */
    @EventHandler
    public void onBlockBurn(LeavesDecayEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
            if (storedItem != null) {
                CustomItems.removeStoredBlockItem(block.getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!event.isCancelled()) {
            if (event.getNewState().getType().equals(Material.AIR)) {
                Block block = event.getBlock();
                CustomItem storedItem = CustomItems.getStoredBlockItem(block.getLocation());
                if (storedItem != null) {
                    CustomItems.removeStoredBlockItem(block.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void test(EntityChangeBlockEvent event) {

    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            String customItemID = getCustomItemID(event.getItemInHand());
            if (customItemID != null && !customItemID.isEmpty()) {
                CustomItem customItem = CustomItems.getCustomItem(customItemID);
                if (customItem != null && customItem.create().getType().isBlock()) {
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
    }

    private String getCustomItemID(ItemStack itemStack) {
        if (!ItemUtils.isAirOrNull(itemStack)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (itemMeta.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING)) {
                    return itemMeta.getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING);
                }
            }
        }
        return "";
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlaceMulti(BlockMultiPlaceEvent event) {
        if (!event.isCancelled()) {
            String customItemID = getCustomItemID(event.getItemInHand());
            if (customItemID != null && !customItemID.isEmpty()) {
                CustomItem customItem = CustomItems.getCustomItem(me.wolfyscript.utilities.api.utils.NamespacedKey.getByString(customItemID));
                event.getReplacedBlockStates().forEach(state -> CustomItems.setStoredBlockItem(state.getLocation(), customItem));
            }
        }
    }
}
