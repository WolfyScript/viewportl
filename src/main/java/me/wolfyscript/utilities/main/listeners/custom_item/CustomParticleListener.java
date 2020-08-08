package me.wolfyscript.utilities.main.listeners.custom_item;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        if (CustomItems.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
            CustomItems.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        }
        if (item != null && item.getApiReference() instanceof WolfyUtilitiesRef) {
            ParticleContent particleContent = item.getParticleContent();
            if (particleContent != null) {
                NamespacedKey particleID = particleContent.getParticleEffect(ParticleEffect.Action.HAND);
                if (particleID != null) {
                    CustomItems.setActiveParticleEffect(player, EquipmentSlot.HAND, ParticleEffects.spawnEffectOnPlayer(particleID, EquipmentSlot.HAND, player));
                }
            }
        }
    }

    @EventHandler
    public void onSwitch(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (CustomItems.hasActiveItemEffects(player, EquipmentSlot.HAND)) {
            CustomItems.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        }
        if (CustomItems.hasActiveItemEffects(player, EquipmentSlot.OFF_HAND)) {
            CustomItems.stopActiveParticleEffect(player, EquipmentSlot.OFF_HAND);
        }
        CustomItem mainHand = CustomItem.getByItemStack(event.getMainHandItem());
        if (mainHand != null && mainHand.getApiReference() instanceof WolfyUtilitiesRef) {
            ParticleContent particleContent = mainHand.getParticleContent();
            if (particleContent != null) {
                NamespacedKey particleID = particleContent.getParticleEffect(ParticleEffect.Action.HAND);
                if (particleID != null) {
                    CustomItems.setActiveParticleEffect(player, EquipmentSlot.HAND, ParticleEffects.spawnEffectOnPlayer(particleID, EquipmentSlot.HAND, player));
                }
            }
        }

        CustomItem offHand = CustomItem.getByItemStack(event.getOffHandItem());
        if (offHand != null && offHand.getApiReference() instanceof WolfyUtilitiesRef) {
            ParticleContent particleContent = offHand.getParticleContent();
            if (particleContent != null) {
                NamespacedKey particleID = particleContent.getParticleEffect(ParticleEffect.Action.OFF_HAND);
                if (particleID != null) {
                    CustomItems.setActiveParticleEffect(player, EquipmentSlot.OFF_HAND, ParticleEffects.spawnEffectOnPlayer(particleID, EquipmentSlot.OFF_HAND, player));
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){

    }

}
