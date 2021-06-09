package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GUIInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof GUIInventory<?> inventory) {
            inventory.onClick(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrag(InventoryDragEvent event) {
        if (event.getInventory() instanceof GUIInventory<?> inventory) {
            inventory.onDrag(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() instanceof GUIInventory<?> inventory) {
            inventory.onClose(event);
        }
    }

}
