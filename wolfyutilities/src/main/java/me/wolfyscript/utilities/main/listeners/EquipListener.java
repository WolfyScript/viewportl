package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.inventory.custom_items.ArmorType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.events.ArmorEquipEvent;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
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

import java.util.Optional;
import java.util.stream.Stream;

public class EquipListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getClick().isCreativeAction()
                || event.getAction().equals(InventoryAction.NOTHING)
                || !(event.getWhoClicked() instanceof Player player)
                || event.getClickedInventory() == null
                || !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            return;
        }
        var inv = event.getClickedInventory();

        ItemStack currentItem = event.getCurrentItem();
        var customCurrentItem = CustomItem.getByItemStack(currentItem);

        if (event.isShiftClick() && !event.getClick().isKeyboardClick()) {
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                //Unequip
                var equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, ArmorType.getBySlot(event.getSlot()), currentItem, null, customCurrentItem, null);
                Bukkit.getPluginManager().callEvent(equipEvent);
                event.setCancelled(equipEvent.isCancelled());
            } else {
                //Equip
                if (!ItemUtils.isAirOrNull(customCurrentItem) && customCurrentItem.hasEquipmentSlot()) {
                    Optional<ArmorType> optionalArmorType = Stream.of(ArmorType.values()).filter(t -> ItemUtils.isAirOrNull(inv.getItem(t.getSlot())) && customCurrentItem.hasEquipmentSlot(t.getEquipmentSlot())).findFirst();
                    if (optionalArmorType.isPresent()) {
                        var type = optionalArmorType.get();
                        var equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, type, null, currentItem, null, customCurrentItem);
                        Bukkit.getPluginManager().callEvent(equipEvent);
                        if (equipEvent.isCancelled()) {
                            event.setCancelled(true);
                        } else if (!equipEvent.canBeEquippedVanilla()) {
                            event.setCancelled(true);
                            inv.setItem(type.getSlot(), equipEvent.getNewArmorPiece());
                            inv.setItem(event.getSlot(), equipEvent.getOldArmorPiece());
                        }
                    }
                }
            }
        } else if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
            // Prevents checking clicks in the 2x2 crafting grid
            var type = ArmorType.getBySlot(event.getSlot());
            ItemStack cursorItem = event.getCursor();
            if (type != null) {
                if (event.getClick().equals(ClickType.NUMBER_KEY)) {
                    // getClickedInventory() == The players inventory
                    // getHotBarButton() == key players are pressing to equip or unequip the item to or from.
                    // getRawSlot() == The slot the item is going to.
                    // getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot
                    var hotbarItem = inv.getItem(event.getHotbarButton());
                    var method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                    var equipEvent = !ItemUtils.isAirOrNull(hotbarItem) ? new ArmorEquipEvent(player, method, type, inv.getItem(event.getSlot()), hotbarItem) : new ArmorEquipEvent(player, method, type, currentItem, cursorItem);
                    Bukkit.getPluginManager().callEvent(equipEvent);
                    if (equipEvent.isCancelled()) {
                        event.setCancelled(true);
                    } else if (!equipEvent.canBeEquippedVanilla()) {
                        event.setCancelled(true);
                        inv.setItem(event.getHotbarButton(), equipEvent.getOldArmorPiece());
                        inv.setItem(event.getSlot(), equipEvent.getNewArmorPiece());
                    }
                } else if (event.getClick().equals(ClickType.DROP) || event.getClick().equals(ClickType.CONTROL_DROP)) {
                    var equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.DROP, type, currentItem, cursorItem);
                    Bukkit.getPluginManager().callEvent(equipEvent);
                    event.setCancelled(equipEvent.isCancelled());
                } else {
                    // e.getCurrentItem() == Unequip
                    // e.getCursor() == Equip
                    var equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.PICK_DROP, type, currentItem, cursorItem);
                    Bukkit.getPluginManager().callEvent(equipEvent);
                    event.setCancelled(true);
                    if (!equipEvent.isCancelled()) {
                        InventoryUtils.calculateClickedSlot(event, equipEvent.getNewArmorPiece(), equipEvent.getOldArmorPiece());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.useItemInHand().equals(Event.Result.DENY)) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            var player = e.getPlayer();
            if (!e.useInteractedBlock().equals(Event.Result.DENY)) {
                // Having both of these checks is useless, might as well do it though.
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && !player.isSneaking() && e.getClickedBlock().getType().isInteractable()) {
                    // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
                    return;
                }
            }
            var customItem = CustomItem.getByItemStack(e.getItem());
            var armorType = ArmorType.matchType(e.getItem(), customItem, player.getInventory());
            if (armorType != null) {
                var equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR, armorType, null, e.getItem(), null, customItem);
                Bukkit.getPluginManager().callEvent(equipEvent);
                if (equipEvent.isCancelled()) {
                    e.setCancelled(true);
                    player.updateInventory();
                    return;
                }
                if (customItem != null && customItem.hasEquipmentSlot()) {
                    if (customItem.hasEquipmentSlot()) {
                        e.setCancelled(true);
                        ItemStack newArmor = equipEvent.getNewArmorPiece().clone();
                        newArmor.setAmount(1);
                        player.getInventory().setItem(equipEvent.getType().getSlot(), newArmor);
                        equipEvent.getNewArmorPiece().setAmount(equipEvent.getNewArmorPiece().getAmount() - 1);
                    } else if (customItem.isBlockVanillaEquip()) {
                        e.setCancelled(true);
                    }
                    player.updateInventory();
                }
            }
        }
    }

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
        //*/
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
        //*
        System.out.println("- Equipment-Test -");
        System.out.println("    New: " + event.getNewArmorPiece());
        System.out.println("    Old: " + event.getOldArmorPiece());
        System.out.println("    ArmorType: " + event.getType());
        System.out.println("    Method: " + event.getMethod());
        //*/
    }
}
