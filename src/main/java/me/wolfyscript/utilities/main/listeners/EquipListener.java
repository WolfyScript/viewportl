package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Item;
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

import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

public class EquipListener implements Listener {

    @EventHandler
    public void dispenseArmorEvent(BlockDispenseEvent event){
        CustomItem customItem = CustomItem.getByItemStack(event.getItem());
        if(customItem != null && customItem.hasID() && customItem.hasEquipmentSlot()){
            Location location = event.getBlock().getLocation();
            //TODO Get Player infront of Dispenser!
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickInventory(InventoryClickEvent event) {
        boolean shift = event.isShiftClick(), numberkey = event.getClick().equals(ClickType.NUMBER_KEY);

        if(!(event.getWhoClicked() instanceof Player)) return;
        if(event.getClickedInventory() != null && !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        CustomItem item = CustomItem.getByItemStack(event.isShiftClick() ? event.getCurrentItem() : event.getCursor());

        if(item == null) return;
        System.out.println("Check if valid...");
        //if(!item.hasEquipmentSlot() && !ItemUtils.isEquipable(item.getType())) return;
        System.out.println("item valid");

        boolean unequip = false;

        if(shift){
            if (!event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                //Equip
                System.out.println("Shift Click equip");

            }else{
                //Unequip
                unequip = true;
                System.out.println("Shift Click unequip");


            }


        }else{
            CustomItem newArmorPiece = CustomItem.getByItemStack(event.getCursor());
            CustomItem oldArmorPiece = CustomItem.getByItemStack(event.getCurrentItem());
            if(numberkey){
                if(event.getClickedInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
                    // e.getClickedInventory() == The players inventory
                    // e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
                    // e.getRawSlot() == The slot the item is going to.
                    // e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;

                    CustomItem hotbarItem = CustomItem.getByItemStack(event.getClickedInventory().getItem(event.getHotbarButton()));
                    if(!ItemUtils.isAirOrNull(hotbarItem)){
                        //Equip

                        newArmorPiece = hotbarItem;
                        oldArmorPiece = CustomItem.getByItemStack(event.getClickedInventory().getItem(event.getSlot()));

                        System.out.println("NumberKey equip slot: "+event.getRawSlot());
                    }else{
                        //Unequip
                        unequip = true;

                        System.out.println("NumberKey unequip slot: "+event.getRawSlot());
                    }
                }
            }else{
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                    System.out.println("Normal");
                    if(ItemUtils.isAirOrNull(event.getCursor()) && !ItemUtils.isAirOrNull(event.getCurrentItem())){// unequip with no new item going into the slot.
                        unequip = true;
                        System.out.println("Normal unequip");

                    }else{
                        System.out.println("Normal equip");



                    }
                }
                // newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
            }

        }

        /*
        if (cursor != null && cursor.getType().equals(Material.IRON_HOE)) {
            if (cursor.hasItemMeta() && cursor.getItemMeta().hasCustomModelData()) {
                if (event.getSlotType().equals(InventoryType.SlotType.ARMOR) && event.getSlot() == 39) {
                    event.getClickedInventory().setItem(event.getSlot(), event.getCursor());
                    event.getView().setCursor(new ItemStack(Material.AIR));
                    event.setCancelled(true);
                }
            }
        } else if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.IRON_HOE)) {
            ItemStack currentItem = event.getCurrentItem();
            if(event.getView().getTopInventory().getType().equals(InventoryType.CRAFTING)){
                if (currentItem.hasItemMeta() && currentItem.hasItemMeta()) {
                    if (event.isShiftClick() && !event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                        if (event.getInventory().getItem(39) == null || event.getInventory().getItem(39).getType().equals(Material.AIR)) {
                            event.getClickedInventory().setItem(39, event.getCurrentItem());
                            event.setCurrentItem(null);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        */
    }


    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerInteractEvent(PlayerInteractEvent e){
        if(e.getAction() == Action.PHYSICAL) return;
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = e.getPlayer();
            if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){// Having both of these checks is useless, might as well do it though.
                // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
                if(e.getClickedBlock().getType().isInteractable()){
                    return;
                }
            }
            CustomItem customItem = CustomItem.getByItemStack(e.getItem());
            if(customItem != null && customItem.hasID() && customItem.hasEquipmentSlot()){



            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event){
        // getType() seems to always be even.
        // Old Cursor gives the item you are equipping
        // Raw slot is the ArmorType slot
        // Can't replace armor using this method making getCursor() useless.

    }


}
