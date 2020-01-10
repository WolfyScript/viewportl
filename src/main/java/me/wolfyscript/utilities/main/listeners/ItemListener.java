package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;

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
}
