package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.custom_data.ParticleData;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemListener implements Listener {

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();
        if (WolfyUtilities.hasVillagePillageUpdate() && CustomItem.hasCustomDurability(itemStack)) {
            event.setCancelled(true);
            int totalDmg = CustomItem.getCustomDamage(itemStack) + event.getDamage();
            if (totalDmg > CustomItem.getCustomDurability(itemStack)) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            } else {
                CustomItem.setCustomDamage(itemStack, CustomItem.getCustomDamage(itemStack) + event.getDamage());
            }
        } else if (ItemUtils.hasCustomDurability(itemStack)) {
            event.setCancelled(true);
            int totalDmg = ItemUtils.getDamage(itemStack) + event.getDamage();
            int durability = ItemUtils.getCustomDurability(itemStack);

            if (totalDmg > durability) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            } else {
                if(WolfyUtilities.hasVillagePillageUpdate()){
                    //Migrate old data to new 1.14 format!
                    migrateCustomDurability(itemStack, totalDmg, durability);
                }else{
                    ItemUtils.setDamage(itemStack, totalDmg);
                }
            }
        }
    }

    @EventHandler
    public void onMend(PlayerItemMendEvent event) {
        ItemStack itemStack = event.getItem();
        if (WolfyUtilities.hasVillagePillageUpdate() && CustomItem.hasCustomDurability(itemStack)) {
            int totalDmg = CustomItem.getCustomDamage(itemStack) - event.getRepairAmount();
            if (!(totalDmg >= 0)) {
                totalDmg = 0;
            }
            CustomItem.setCustomDamage(itemStack, totalDmg);
        } else if (ItemUtils.hasCustomDurability(itemStack)) {
            int totalDmg = ItemUtils.getDamage(itemStack) - event.getRepairAmount();
            if (!(totalDmg >= 0)) {
                totalDmg = 0;
            }
            if(WolfyUtilities.hasVillagePillageUpdate()){
                //Migrate old data to new 1.14 format!
                migrateCustomDurability(itemStack, ItemUtils.getCustomDurability(itemStack), totalDmg);
            }else{
                ItemUtils.setDamage(itemStack, totalDmg);
            }
        }
    }

    private static void migrateCustomDurability(ItemStack itemStack, int totalDmg, int durability){
        CustomItem.setCustomDurabilityTag(itemStack, ItemUtils.getDurabilityTag(itemStack).replace("%DUR%","%dur%").replace("%MAX_DUR%", "%max_dur%"));
        CustomItem.setCustomDurability(itemStack, durability);
        CustomItem.setCustomDamage(itemStack, totalDmg);
        ItemUtils.removeItemSettings(itemStack);
        ItemUtils.removeDurabilityTag(itemStack);
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event){
        if(WolfyUtilities.hasVillagePillageUpdate()){
            Player player = event.getPlayer();
            PlayerInventory playerInventory = player.getInventory();
            ItemStack previouseItem = playerInventory.getItem(event.getPreviousSlot());
            CustomItem previousCustomItem = CustomItem.getByItemStack(previouseItem);
            if(previousCustomItem != null && previousCustomItem.hasID()){
                CustomItems.stopActiveParticleEffect(player, previousCustomItem);
                ParticleData particleData = (ParticleData) previousCustomItem.getCustomData("particle_data");

                String particleID = particleData.getParticleEffect(ParticleEffect.Action.OFF_HAND);
                if(particleID != null){
                    CustomItems.setActiveParticleEffect(player, previousCustomItem, ParticleEffects.spawnEffectOnPlayer(particleID, EquipmentSlot.OFF_HAND, player));
                }
            }

            ItemStack newItem = playerInventory.getItem(event.getNewSlot());
            CustomItem item = CustomItem.getByItemStack(newItem);
            if(item != null && item.hasID()) {
                CustomItems.stopActiveParticleEffect(player, item);
                ParticleData particleData = (ParticleData) item.getCustomData("particle_data");

                String particleID = particleData.getParticleEffect(ParticleEffect.Action.HAND);
                if (particleID != null) {
                    CustomItems.setActiveParticleEffect(player, item, ParticleEffects.spawnEffectOnPlayer(particleID, EquipmentSlot.HAND, player));
                }
            }
        }
    }
}
