package me.wolfyscript.utilities.util.inventory;

import com.google.common.collect.Streams;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class InventoryUtils {

    public static boolean isEmpty(@Nullable List<ItemStack> list) {
        if (list == null) return false;
        return list.stream().allMatch(ItemUtils::isAirOrNull);
    }

    public static boolean isCustomItemsListEmpty(@Nullable List<CustomItem> list) {
        if (list == null) return false;
        return list.stream().allMatch(ItemUtils::isAirOrNull);
    }

    public static int getInventorySpace(Player p, ItemStack item) {
        return getInventorySpace(p.getInventory(), item);
    }

    public static int getInventorySpace(Inventory inventory, ItemStack item) {
        return getInventorySpace(inventory.getStorageContents(), item);
    }

    public static int getInventorySpace(ItemStack[] contents, ItemStack item) {
        int free = 0;
        for (ItemStack i : contents) {
            if (ItemUtils.isAirOrNull(i)) {
                free += item.getMaxStackSize();
            } else if (i.isSimilar(item)) {
                free += item.getMaxStackSize() - i.getAmount();
            }
        }
        return free;
    }

    public static boolean hasInventorySpace(Inventory inventory, ItemStack itemStack, int amount) {
        return getInventorySpace(inventory, itemStack) >= itemStack.getAmount() * amount;
    }

    public static boolean hasInventorySpace(Inventory inventory, ItemStack itemStack) {
        return hasInventorySpace(inventory.getStorageContents(), itemStack);
    }

    public static boolean hasInventorySpace(ItemStack[] contents, ItemStack itemStack) {
        return getInventorySpace(contents, itemStack) >= itemStack.getAmount();
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
            if (slotItem == null) {
                return i;
            }
            if (itemStack.isSimilar(slotItem)) {
                if (slotItem.getAmount() + itemStack.getAmount() <= slotItem.getMaxStackSize()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void calculateClickedSlot(InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor();
        if (cursor == null) return;
        final ItemStack fuel = event.getCurrentItem();
        if (event.getClick().equals(ClickType.LEFT)) {
            if (!ItemUtils.isAirOrNull(fuel)) {
                event.setCancelled(true);
                if (fuel.isSimilar(cursor)) {
                    int possibleAmount = fuel.getMaxStackSize() - fuel.getAmount();
                    fuel.setAmount(fuel.getAmount() + (Math.min(cursor.getAmount(), possibleAmount)));
                    cursor.setAmount(cursor.getAmount() - possibleAmount);
                    event.setCurrentItem(fuel);
                    event.setCursor(cursor);
                } else {
                    event.setCursor(fuel);
                    event.setCurrentItem(cursor);
                }
            } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                event.setCancelled(true);
                event.setCursor(ItemUtils.AIR);
                event.setCurrentItem(cursor);
            }
        } else {
            if (!ItemUtils.isAirOrNull(fuel)) {
                if (fuel.isSimilar(cursor)) {
                    if (fuel.getAmount() < fuel.getMaxStackSize() && cursor.getAmount() > 0) {
                        event.setCancelled(true);
                        fuel.setAmount(fuel.getAmount() + 1);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                } else {
                    event.setCancelled(true);
                    event.setCursor(fuel);
                    event.setCurrentItem(cursor);
                }
            } else {
                event.setCancelled(true);
                ItemStack itemStack = cursor.clone();
                cursor.setAmount(cursor.getAmount() - 1);
                itemStack.setAmount(1);
                event.setCurrentItem(itemStack);
            }
        }
        if (event.getWhoClicked() instanceof Player) {
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }
}
