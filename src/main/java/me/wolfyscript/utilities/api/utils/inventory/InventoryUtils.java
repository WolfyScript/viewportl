package me.wolfyscript.utilities.api.utils.inventory;

import com.google.common.collect.Streams;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class InventoryUtils {

    public static boolean isEmpty(@Nullable List<ItemStack> list) {
        if (list == null) return false;
        return list.stream().noneMatch(item -> item != null && !item.getType().equals(Material.AIR));
    }

    public static boolean isCustomItemsListEmpty(@Nullable List<CustomItem> list) {
        if (list == null) return false;
        return list.stream().noneMatch(item -> item != null && !item.getItemStack().getType().equals(Material.AIR));
    }

    public static int getInventorySpace(Player p, ItemStack item) {
        return getInventorySpace(p.getInventory(), item);
    }

    public static int getInventorySpace(Inventory inventory, ItemStack item) {
        int free = 0;
        for (ItemStack i : inventory.getStorageContents()) {
            if (ItemUtils.isAirOrNull(i)) {
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
        return Streams.stream(p.getInventory()).filter(Objects::isNull).count() >= count;
    }

    public static int firstSimilar(Inventory inventory, ItemStack itemStack){
        for(int i = 0; i < inventory.getSize(); i++){
            ItemStack slotItem = inventory.getItem(i);
            if(slotItem == null){
                return i;
            }
            if(slotItem.isSimilar(itemStack) || itemStack.isSimilar(slotItem)){
                if(slotItem.getAmount() + itemStack.getAmount() <= slotItem.getMaxStackSize()){
                    return i;
                }
            }
        }
        return -1;
    }
}
