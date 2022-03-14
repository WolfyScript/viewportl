/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util.inventory;

import com.google.common.collect.Streams;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.entity.Player;
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

    public static int firstSimilar(Inventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
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
        calculateClickedSlot(event, event.getCursor(), event.getCurrentItem());
    }

    public static void calculateClickedSlot(final InventoryClickEvent event, final ItemStack cursor, final ItemStack currentItem) {
        if (cursor == null) return;
        if (event.getClick().isLeftClick()) {
            if (!ItemUtils.isAirOrNull(currentItem)) {
                event.setCancelled(true);
                if (currentItem.isSimilar(cursor)) {
                    int possibleAmount = currentItem.getMaxStackSize() - currentItem.getAmount();
                    currentItem.setAmount(currentItem.getAmount() + (Math.min(cursor.getAmount(), possibleAmount)));
                    cursor.setAmount(cursor.getAmount() - possibleAmount);
                    event.setCurrentItem(currentItem);
                    event.setCursor(cursor);
                } else {
                    event.setCursor(currentItem);
                    event.setCurrentItem(cursor);
                }
            } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                event.setCancelled(true);
                event.setCursor(ItemUtils.AIR);
                event.setCurrentItem(cursor);
            }
        } else if (event.getClick().isRightClick()) {
            if (!ItemUtils.isAirOrNull(currentItem)) {
                if (currentItem.isSimilar(cursor)) {
                    if (currentItem.getAmount() < currentItem.getMaxStackSize() && cursor.getAmount() > 0) {
                        event.setCancelled(true);
                        currentItem.setAmount(currentItem.getAmount() + 1);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                } else {
                    event.setCancelled(true);
                    event.setCursor(currentItem);
                    event.setCurrentItem(cursor);
                }
            } else {
                event.setCancelled(true);
                var itemStack = cursor.clone();
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
