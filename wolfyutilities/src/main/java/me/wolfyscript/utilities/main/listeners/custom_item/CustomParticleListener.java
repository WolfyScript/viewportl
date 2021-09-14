package me.wolfyscript.utilities.main.listeners.custom_item;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CustomParticleListener implements Listener {

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        var player = event.getPlayer();
        var playerInventory = player.getInventory();
        var newItem = playerInventory.getItem(event.getNewSlot());
        var item = CustomItem.getByItemStack(newItem);
        if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
            PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        }
        if (item != null) {
            item.getParticleContent().spawn(player, EquipmentSlot.HAND);
        }
    }

    @EventHandler
    public void onSwitch(PlayerSwapHandItemsEvent event) {
        var player = event.getPlayer();
        if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
            PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        }
        if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.OFF_HAND)) {
            PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.OFF_HAND);
        }
        var mainHand = CustomItem.getByItemStack(event.getMainHandItem());
        if (mainHand != null) {
            mainHand.getParticleContent().spawn(player, EquipmentSlot.HAND);
        }
        var offHand = CustomItem.getByItemStack(event.getOffHandItem());
        if (offHand != null) {
            offHand.getParticleContent().spawn(player, EquipmentSlot.OFF_HAND);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        var player = event.getPlayer();
        var droppedItem = event.getItemDrop().getItemStack();
        var currentItem = player.getInventory().getItemInMainHand();

        if (currentItem.getType().equals(Material.AIR) || currentItem.getAmount() <= 0) {
            if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
                PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {

    }

}
