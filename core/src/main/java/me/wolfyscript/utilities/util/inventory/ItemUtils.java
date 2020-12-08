package me.wolfyscript.utilities.util.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.equipment.ArmorType;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtils {

    private final WolfyUtilities wolfyUtilities;

    public ItemUtils(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
    }

    //SOME ITEMMETA UTILS
    public static boolean isEquipable(Material material) {
        if (material.name().endsWith("_CHESTPLATE") || material.name().endsWith("_LEGGINGS") || material.name().endsWith("_HELMET") || material.name().endsWith("_BOOTS") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL")) {
            return true;
        }
        switch (material) {
            case ELYTRA:
            case CARVED_PUMPKIN:
                return true;
        }
        return false;
    }

    public static boolean isEquipable(Material material, ArmorType type) {
        switch (type) {
            case HELMET:
                return material.name().endsWith("_HELMET") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL") || material.equals(Material.CARVED_PUMPKIN);
            case CHESTPLATE:
                return material.equals(Material.ELYTRA) || material.name().endsWith("_CHESTPLATE");
            case LEGGINGS:
                return material.name().endsWith("_LEGGINGS");
            case BOOTS:
                return material.name().endsWith("_BOOTS");
        }
        return false;
    }

    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    public static boolean isAirOrNull(CustomItem item) {
        return item == null || item.getApiReference() == null || isAirOrNull(item.getItemStack());
    }

    public static boolean isTool(Material material) {
        return material.name().endsWith("AXE") || material.name().endsWith("HOE") || material.name().endsWith("SWORD") || material.name().endsWith("SHOVEL") || material.name().endsWith("PICKAXE");
    }

    /*
    Prepare and configure the ItemStack for the GUI!
     */
    public static ItemStack[] createItem(ItemStack itemStack, String displayName, String[] helpLore, String... normalLore) {
        ItemStack[] itemStacks = new ItemStack[2];
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (itemMeta != null) {
            if (displayName != null && !displayName.isEmpty()) {
                itemMeta.setDisplayName(ChatColor.convert(displayName));
            }
            if (normalLore != null && normalLore.length > 0) {
                lore = Arrays.stream(normalLore).map(row -> row.equalsIgnoreCase("<empty>") ? "" : ChatColor.convert(row)).collect(Collectors.toList());
                itemMeta.setLore(lore);
            }
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemStack.setItemMeta(itemMeta);
        }
        itemStacks[0] = itemStack;
        ItemStack helpItem = new ItemStack(itemStack);
        ItemMeta helpMeta = helpItem.getItemMeta();
        if (helpMeta != null) {
            if (helpLore != null && helpLore.length > 0) {
                lore.addAll(Arrays.stream(helpLore).map(row -> row.equalsIgnoreCase("<empty>") ? "" : ChatColor.convert(row)).collect(Collectors.toList()));
            }
            helpMeta.setLore(lore);
            helpItem.setItemMeta(helpMeta);
        }
        itemStacks[1] = helpItem;
        return itemStacks;
    }

    public ItemStack translateItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (wolfyUtilities.getLanguageAPI().getActiveLanguage() != null) {
                        displayName = wolfyUtilities.getLanguageAPI().replaceKeys(displayName);
                    }
                    itemMeta.setDisplayName(ChatColor.convert(displayName));
                }
                if (itemMeta.hasLore() && wolfyUtilities.getLanguageAPI().getActiveLanguage() != null) {
                    List<String> newLore = new ArrayList<>();
                    for (String row : itemMeta.getLore()) {
                        if (row.startsWith("[WU]")) {
                            newLore.add(wolfyUtilities.getLanguageAPI().replaceKeys(row.substring("[WU]".length())));
                        } else if (row.startsWith("[WU!]")) {
                            for (String newRow : wolfyUtilities.getLanguageAPI().replaceKey(row.substring("[WU!]".length()))) {
                                newLore.add(ChatColor.convert(newRow));
                            }
                        } else {
                            newLore.add(row);
                        }
                    }
                    itemMeta.setLore(newLore);
                }
                itemStack.setItemMeta(itemMeta);
            }
        }
        return itemStack;
    }
}
