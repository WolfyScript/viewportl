package me.wolfyscript.utilities.api.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryUtils {

    public static boolean isEmpty(List<ItemStack> list) {
        for (ItemStack itemStack : list) {
            if (!itemStack.getType().equals(Material.AIR)) {
                return false;
            }
        }
        return true;
    }

    public static int getInventorySpace(Player p, ItemStack item) {
        return getInventorySpace(p.getInventory(), item);
    }

    public static int getInventorySpace(Inventory inventory, ItemStack item) {
        int free = 0;
        for (ItemStack i : inventory.getStorageContents()) {
            if (i == null || i.getType().equals(Material.AIR)) {
                free += item.getMaxStackSize();
            } else if (i.isSimilar(item)) {
                free += item.getMaxStackSize() - i.getAmount();
            }
        }
        return free;
    }

    public static boolean hasInventorySpace(Inventory inventory, ItemStack itemStack) {
        return getInventorySpace(inventory, itemStack) >= itemStack.getAmount();
    }

    public static boolean hasInventorySpace(Player p, ItemStack item) {
        return getInventorySpace(p, item) >= item.getAmount();
    }

    public static boolean hasEmptySpaces(Player p, int count) {
        int empty = 0;
        for (ItemStack i : p.getInventory()) {
            if (i == null) {
                empty++;
            }
        }
        return empty >= count;
    }
}
