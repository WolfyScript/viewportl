package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemListener implements Listener {

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();
        if (ItemUtils.hasCustomDurability(itemStack)) {
            event.setCancelled(true);
            int totalDmg = ItemUtils.getDamage(itemStack) + event.getDamage();
            if(totalDmg > ItemUtils.getCustomDurability(itemStack)){
                itemStack.setAmount(itemStack.getAmount()-1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            }else{
                ItemUtils.setDamage(itemStack, ItemUtils.getDamage(itemStack) + event.getDamage());
            }
        }
    }

    @EventHandler
    public void onMend(PlayerItemMendEvent event){
        ItemStack itemStack = event.getItem();
        if (ItemUtils.hasCustomDurability(itemStack)) {
            int totalDmg = ItemUtils.getDamage(itemStack) - event.getRepairAmount();
            if(!(totalDmg >= 0)){
                totalDmg = 0;
            }
            ItemUtils.setDamage(itemStack, totalDmg);
        }
    }
}
