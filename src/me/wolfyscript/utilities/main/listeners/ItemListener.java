package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event){
        ItemStack itemStack = event.getItem();
        if(ItemUtils.hasCustomDurability(itemStack)){
            event.setCancelled(true);
            ItemUtils.setDamage(itemStack, ItemUtils.getDamage(itemStack)+event.getDamage());
        }
    }
}
