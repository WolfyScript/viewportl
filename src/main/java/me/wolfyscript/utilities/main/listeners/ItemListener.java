package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
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
}
