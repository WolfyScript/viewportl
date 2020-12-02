package me.wolfyscript.utilities.main.listeners.custom_item;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;

public class CustomDurabilityListener implements Listener {

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        CustomItem customItem = new CustomItem(event.getItem());
        ItemStack itemStack = event.getItem();

        if (customItem.hasCustomDurability()) {
            event.setCancelled(true);
            int totalDmg = customItem.getCustomDamage() + event.getDamage();
            if (totalDmg > customItem.getCustomDurability()) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            } else {
                customItem.setCustomDamage(customItem.getCustomDamage() + event.getDamage());
            }
        }
    }

    @EventHandler
    public void onMend(PlayerItemMendEvent event) {
        CustomItem customItem = new CustomItem(event.getItem());
        if (customItem.hasCustomDurability()) {
            int repairAmount = Math.min(event.getExperienceOrb().getExperience() * 2, customItem.getCustomDamage());
            int totalDmg = customItem.getCustomDamage() - repairAmount;
            if (!(totalDmg >= 0)) {
                totalDmg = 0;
            }
            int finalTotalDmg = totalDmg;
            Bukkit.getScheduler().runTask(WUPlugin.getInstance(), () -> customItem.setCustomDamage(finalTotalDmg));
        }
    }
}
