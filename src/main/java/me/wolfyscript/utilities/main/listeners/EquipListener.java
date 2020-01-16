package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.equipment.ArmorEquipEvent;
import me.wolfyscript.utilities.api.custom_items.equipment.ArmorType;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class EquipListener implements Listener {

    @EventHandler
    public void dispenseArmorEvent(BlockDispenseEvent event) {
        CustomItem customItem = CustomItem.getByItemStack(event.getItem());
        if (customItem != null && customItem.hasID() && customItem.hasEquipmentSlot()) {
            Location location = event.getBlock().getLocation();
            //TODO Get Player infront of Dispenser!
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArmorEquip(ArmorEquipEvent event){
        if(!ItemUtils.isAirOrNull(event.getNewArmorPiece())){
            CustomItem newItem = event.getNewArmorPiece();
            if(ItemUtils.isEquipable(newItem.getType())){
                if(newItem.getType().name().endsWith("_"+event.getType().name()) && newItem.isBlockVanillaEquip()){
                    event.setCancelled(true);
                }
            }else{
                event.setCancelled(true);
            }
            if(newItem.hasID() && newItem.getEquipmentSlots().contains(event.getType().getEquipmentSlot())){
                //TODO START AND CANCEL PARTICLE EFFECTS
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickInventory(InventoryClickEvent event) {
        boolean shift = event.isShiftClick(), numberkey = event.getClick().equals(ClickType.NUMBER_KEY);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && !event.getClickedInventory().getType().equals(InventoryType.PLAYER))
            return;
        CustomItem item = CustomItem.getByItemStack(event.isShiftClick() ? event.getCurrentItem() : event.getCursor());

        if (item == null) return;
        //if(!item.hasEquipmentSlot() && !ItemUtils.isEquipable(item.getType())) return;

        ArmorType armorType = null;

        if (shift) {
            CustomItem newArmorPiece = CustomItem.getByItemStack(event.getCursor());
            if (!event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                //Equip
                System.out.println("Shift Click equip");
                System.out.println("    " + event.getSlot());
                System.out.println("    " + event.getRawSlot());
                //TODO Compute shift clicking!
                if(!ItemUtils.isAirOrNull(newArmorPiece)){
                    if(newArmorPiece.hasEquipmentSlot()){
                        int slot = -1;
                        for(int i = 39; i > 36; i--){
                            if(ItemUtils.isAirOrNull(event.getClickedInventory().getItem(i))){
                                slot = i;
                            }
                        }
                        if(slot == -1){
                            event.setCancelled(true);
                            return;
                        }else{
                            event.setCancelled(true);
                            ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, ArmorType.getBySlot(slot), null, newArmorPiece);
                            newArmorPiece = equipEvent.getNewArmorPiece();
                            if(equipEvent.isCancelled()){
                                return;
                            }
                            event.getClickedInventory().setItem(slot, newArmorPiece);
                            event.getClickedInventory().setItem(event.getSlot(), null);

                        }
                    }
                }

            } else {
                //Unequip
                System.out.println("Shift Click unequip");


            }
        } else {
            CustomItem newArmorPiece = CustomItem.getByItemStack(event.getCursor());
            CustomItem oldArmorPiece = CustomItem.getByItemStack(event.getCurrentItem());
            if (numberkey) {
                if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {// Prevents checking clicks in the 2x2 crafting grid
                    // getClickedInventory() == The players inventory
                    // getHotBarButton() == key players are pressing to equip or unequip the item to or from.
                    // getRawSlot() == The slot the item is going to.
                    // getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
                    CustomItem hotbarItem = CustomItem.getByItemStack(event.getClickedInventory().getItem(event.getHotbarButton()));
                    if (!ItemUtils.isAirOrNull(hotbarItem)) {
                        //Equip
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = CustomItem.getByItemStack(event.getClickedInventory().getItem(event.getSlot()));
                    }
                    ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR_SWAP, ArmorType.getBySlot(event.getSlot()),  oldArmorPiece, newArmorPiece);
                    Bukkit.getPluginManager().callEvent(equipEvent);
                    if (equipEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    newArmorPiece = equipEvent.getNewArmorPiece();
                    oldArmorPiece = equipEvent.getOldArmorPiece();
                    if (ItemUtils.isEquipable(newArmorPiece.getType()) && newArmorPiece.getType().name().endsWith("_" + armorType.name())) {
                        return;
                    }
                    event.setCancelled(true);
                    event.getClickedInventory().setItem(event.getSlot(), newArmorPiece.getRealItem());
                    event.getClickedInventory().setItem(event.getHotbarButton(), ItemUtils.isAirOrNull(oldArmorPiece) ? null : oldArmorPiece.getRealItem());
                }
            } else {
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                    if (ItemUtils.isAirOrNull(newArmorPiece) && ItemUtils.isAirOrNull(oldArmorPiece)) return;

                    armorType = ArmorType.getBySlot(event.getSlot());
                    if (!ItemUtils.isAirOrNull(newArmorPiece) && ItemUtils.isAirOrNull(oldArmorPiece)) {
                        armorType = ArmorType.getBySlot(event.getSlot());

                        if (ItemUtils.isAirOrNull(oldArmorPiece)) {
                            //Equip new item without any old item
                        } else if (!event.isRightClick()) {
                            //Equip new item and take out old item
                        } else {
                            return;
                        }
                    } else {
                        // unequip with no new item going into the slot.
                    }

                    ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.PICK_DROP, ArmorType.getBySlot(event.getSlot()), oldArmorPiece, newArmorPiece);
                    Bukkit.getPluginManager().callEvent(equipEvent);
                    if (equipEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    newArmorPiece = equipEvent.getNewArmorPiece();
                    oldArmorPiece = equipEvent.getOldArmorPiece();
                    if (ItemUtils.isEquipable(newArmorPiece.getType()) && newArmorPiece.getType().name().endsWith("_" + armorType.name())) {
                        return;
                    }
                    event.setCancelled(true);
                    event.getClickedInventory().setItem(event.getSlot(), newArmorPiece);
                    event.getView().setCursor(oldArmorPiece);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {// Having both of these checks is useless, might as well do it though.
                // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
                if (e.getClickedBlock().getType().isInteractable()) {
                    return;
                }
            }
            CustomItem customItem = CustomItem.getByItemStack(e.getItem());
            if (customItem != null && customItem.hasID() && customItem.hasEquipmentSlot()) {


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


}
