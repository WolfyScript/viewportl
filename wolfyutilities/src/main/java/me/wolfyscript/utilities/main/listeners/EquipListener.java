package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.inventory.custom_items.ArmorType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.events.ArmorEquipEvent;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EquipListener implements Listener {

    @EventHandler
    public void dispenseArmorEvent(BlockDispenseEvent event) {
        /*
        ItemStack item = event.getItem();
        CustomItem customItem = CustomItem.getByItemStack(item);
        if (customItem != null) {
            Block block = event.getBlock();
            Dispenser dispenser = (Dispenser) block.getBlockData();
            BlockFace face = dispenser.getFacing();
            int x = block.getX() + face.getModX();
            int y = block.getY() + face.getModY();
            int z = block.getZ() + face.getModZ();
            BoundingBox boundingBox = new BoundingBox(x, y, z, x+1, y+1, z+1);
            List<Entity> entities = block.getWorld().getNearbyEntities(boundingBox, entity -> entity instanceof Player).stream().collect(Collectors.toList());

            if(entities.size() > 0){
                Entity entity = entities.get(0);
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    ArmorType armorType = ArmorType.matchType(customItem, player.getInventory());
                    if(armorType != null){
                        ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.DISPENSER, armorType, null, item, null, customItem);
                        if(equipEvent.isCancelled()){
                            event.setCancelled(true);
                            return;
                        }

                        player.getInventory().setItem(armorType.getSlot(), equipEvent.getNewArmorPiece());

                        if(customItem != null && customItem.hasEquipmentSlot()){
                            if(customItem.hasEquipmentSlot()){

                                ItemStack newArmor = equipEvent.getNewArmorPiece().clone();
                                newArmor.setAmount(1);
                                player.getInventory().setItem(equipEvent.getType().getSlot(), newArmor);
                                equipEvent.getNewArmorPiece().setAmount(equipEvent.getNewArmorPiece().getAmount() - 1);

                            }else if(customItem.isBlockVanillaEquip()){
                                event.setCancelled(true);
                            }
                            player.updateInventory();
                        }




                    }
                }


            }

        }

         */
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickInventory(InventoryClickEvent event) {
        if(event.getClick().isCreativeAction()) return;
        if (event.getAction().equals(InventoryAction.NOTHING)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;

        boolean shift = event.isShiftClick();
        boolean numberkey = event.getClick().equals(ClickType.NUMBER_KEY);
        Player player = (Player) event.getWhoClicked();

        ItemStack cursorItem = event.getCursor();
        CustomItem customCursorItem = CustomItem.getByItemStack(cursorItem);
        ItemStack currentItem = event.getCurrentItem();
        CustomItem customCurrentItem = CustomItem.getByItemStack(currentItem);

        if (shift) {
            if (!event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                //Equip
                if (!ItemUtils.isAirOrNull(customCurrentItem) && customCurrentItem.hasEquipmentSlot()) {
                    int slot = -1;
                    for (int i = 39; i > 36; i--) {
                        if (ItemUtils.isAirOrNull(event.getClickedInventory().getItem(i)) && customCurrentItem.hasEquipmentSlot(ArmorType.getBySlot(i).getEquipmentSlot())) {
                            slot = i;
                        }
                    }
                    if (slot == -1) return;
                    ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, ArmorType.getBySlot(slot), null, currentItem, null, customCurrentItem);
                    Bukkit.getPluginManager().callEvent(equipEvent);
                    event.setCancelled(true);
                    if (equipEvent.isCancelled()) return;
                    event.getClickedInventory().setItem(slot, equipEvent.getNewArmorPiece());
                    event.getClickedInventory().setItem(event.getSlot(), equipEvent.getOldArmorPiece());
                }
            } else {
                //Unequip
                ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, ArmorType.getBySlot(event.getSlot()), currentItem, null, customCurrentItem, null);
                Bukkit.getPluginManager().callEvent(equipEvent);
                event.setCancelled(equipEvent.isCancelled());
            }
        } else if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
            if (numberkey) {
                // Prevents checking clicks in the 2x2 crafting grid
                // getClickedInventory() == The players inventory
                // getHotBarButton() == key players are pressing to equip or unequip the item to or from.
                // getRawSlot() == The slot the item is going to.
                // getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;

                ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
                CustomItem customHotbarItem = CustomItem.getByItemStack(hotbarItem);
                ArmorType type = ArmorType.getBySlot(event.getSlot());
                if (type == null) return;

                ArmorEquipEvent equipEvent;
                if (!ItemUtils.isAirOrNull(hotbarItem)) {
                    //Equip
                    ItemStack otherItem = event.getClickedInventory().getItem(event.getSlot());
                    equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR_SWAP, ArmorType.getBySlot(event.getSlot()), otherItem, hotbarItem, CustomItem.getByItemStack(otherItem), customHotbarItem);
                }else{
                    equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR_SWAP, ArmorType.getBySlot(event.getSlot()), currentItem, cursorItem, customCurrentItem, customCursorItem);
                }
                Bukkit.getPluginManager().callEvent(equipEvent);
                if (equipEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                ItemStack newArmor = equipEvent.getNewArmorPiece();
                ItemStack oldArmor = equipEvent.getOldArmorPiece();
                if (newArmor != null && ItemUtils.isEquipable(newArmor.getType(), equipEvent.getType())) return;
                event.setCancelled(true);
                event.getClickedInventory().setItem(event.getHotbarButton(), oldArmor);
                event.getClickedInventory().setItem(event.getSlot(), newArmor);

            } else {
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.PICK_DROP, ArmorType.getBySlot(event.getSlot()), currentItem, cursorItem, customCurrentItem, customCursorItem);
                Bukkit.getPluginManager().callEvent(equipEvent);
                event.setCancelled(true);
                if (equipEvent.isCancelled()) return;

                ItemStack newArmor = equipEvent.getNewArmorPiece();
                ItemStack oldArmor = equipEvent.getOldArmorPiece();

                ItemStack newArmor2 = newArmor.clone();
                newArmor2.setAmount(1);
                ItemStack oldArmor2 = oldArmor.clone();
                oldArmor2.setAmount(1);

                if(event.getClick().isRightClick()){
                    //Drop one item / switch
                    if(!newArmor2.equals(oldArmor2)){
                        if(ItemUtils.isAirOrNull(oldArmor)){
                            newArmor.setAmount(newArmor.getAmount() - 1);
                            event.setCurrentItem(newArmor2);
                            return;
                        }
                        if(ItemUtils.isAirOrNull(newArmor)){
                            //Un-equip
                            event.setCancelled(false);
                            return;
                        }
                        event.setCurrentItem(equipEvent.getNewArmorPiece());
                        event.getWhoClicked().setItemOnCursor(equipEvent.getOldArmorPiece());
                        return;
                    }
                    //Equal Items
                    newArmor.setAmount(newArmor.getAmount() - 1);
                    oldArmor.setAmount(oldArmor.getAmount() + 1);
                    return;
                }
                if(event.getClick().isLeftClick()){
                    //Drop all / switch
                    if(!newArmor2.equals(oldArmor2)){
                        event.setCurrentItem(equipEvent.getNewArmorPiece());
                        event.getWhoClicked().setItemOnCursor(equipEvent.getOldArmorPiece());
                        return;
                    }
                    int newAmount = newArmor.getAmount();
                    int oldAmount = oldArmor.getAmount();
                    if(newAmount + oldAmount > oldArmor.getMaxStackSize()){
                        oldArmor.setAmount(oldArmor.getMaxStackSize());
                        newArmor.setAmount(newArmor.getAmount() - (oldArmor.getMaxStackSize() - oldAmount));
                        return;
                    }
                    oldArmor.setAmount(oldAmount + newAmount);
                    newArmor.setAmount(0);
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.useItemInHand().equals(Event.Result.DENY)) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (!e.useInteractedBlock().equals(Event.Result.DENY)) {
                // Having both of these checks is useless, might as well do it though.
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && !player.isSneaking()) {
                    // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
                    if (e.getClickedBlock().getType().isInteractable()) {
                        return;
                    }
                }
            }
            CustomItem customItem = CustomItem.getByItemStack(e.getItem());
            ArmorType armorType = ArmorType.matchType(e.getItem(), customItem, player.getInventory());
            if (armorType != null) {
                ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR, armorType, null, e.getItem(), null, customItem);
                Bukkit.getPluginManager().callEvent(equipEvent);
                if (equipEvent.isCancelled()){
                    e.setCancelled(true);
                    player.updateInventory();
                    return;
                }
                if(customItem != null && customItem.hasEquipmentSlot()){
                    if(customItem.hasEquipmentSlot()){
                        e.setCancelled(true);
                        ItemStack newArmor = equipEvent.getNewArmorPiece().clone();
                        newArmor.setAmount(1);
                        player.getInventory().setItem(equipEvent.getType().getSlot(), newArmor);
                        equipEvent.getNewArmorPiece().setAmount(equipEvent.getNewArmorPiece().getAmount() - 1);
                    }else if(customItem.isBlockVanillaEquip()){
                        e.setCancelled(true);
                    }
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event) {
        // getType() seems to always be even.
        // Old Cursor gives the item you are equipping
        // Raw slot is the ArmorType slot
        // Can't replace armor using this method making getCursor() useless.

    }

    @EventHandler
    public void onEquipTest(ArmorEquipEvent event) {
        /*
        System.out.println("- Equipment-Test -");
        System.out.println("    New: " + event.getNewArmorPiece());
        System.out.println("    Old: " + event.getOldArmorPiece());
        System.out.println("    ArmorType: " + event.getType());
        System.out.println("    Method: " + event.getMethod());
         */

    }


}
