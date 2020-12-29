package me.wolfyscript.utilities.main.listeners.custom_item;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.ParticleContent;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CustomParticleListener implements Listener {

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack newItem = playerInventory.getItem(event.getNewSlot());
        CustomItem item = CustomItem.getByItemStack(newItem);
        if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
            PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        }
        if (item != null) {
            ParticleContent particleContent = item.getParticleContent();
            if (particleContent != null) {
                particleContent.spawn(player, EquipmentSlot.HAND);
            }
        }
    }

    @EventHandler
    public void onSwitch(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
            PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        }
        if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.OFF_HAND)) {
            PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.OFF_HAND);
        }
        CustomItem mainHand = CustomItem.getByItemStack(event.getMainHandItem());
        if (mainHand != null) {
            ParticleContent particleContent = mainHand.getParticleContent();
            if (particleContent != null) {
                particleContent.spawn(player, EquipmentSlot.HAND);
            }
        }
        CustomItem offHand = CustomItem.getByItemStack(event.getOffHandItem());
        if (offHand != null) {
            ParticleContent particleContent = offHand.getParticleContent();
            if (particleContent != null) {
                particleContent.spawn(player, EquipmentSlot.OFF_HAND);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        ItemStack currentItem = player.getInventory().getItemInMainHand();

        if (currentItem.getType().equals(Material.AIR) || currentItem.getAmount() <= 0) {
            if (PlayerUtils.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
                PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();


        }

    }

}
