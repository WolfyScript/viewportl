package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class GUIInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory instanceof GUIInventory) {
            ((GUIInventory<?>) inventory).onClick(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory instanceof GUIInventory) {
            ((GUIInventory<?>) inventory).onDrag(event);
        }
    }

}
