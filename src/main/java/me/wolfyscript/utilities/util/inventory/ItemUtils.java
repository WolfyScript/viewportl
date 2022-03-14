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

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.ArmorType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ItemUtils {

    public final static ItemStack AIR = new ItemStack(Material.AIR);

    private final WolfyUtilities wolfyUtilities;

    public ItemUtils(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
    }

    public static boolean isEquipable(Material material) {
        return switch (material.name()) {
            case "ELYTRA", "CARVED_PUMPKIN" -> true;
            default -> material.name().endsWith("_CHESTPLATE") || material.name().endsWith("_LEGGINGS") || material.name().endsWith("_HELMET") || material.name().endsWith("_BOOTS") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL");
        };
    }

    public static boolean isEquipable(Material material, ArmorType type) {
        return switch (type) {
            case HELMET -> material.name().endsWith("_HELMET") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL") || material.equals(Material.CARVED_PUMPKIN);
            case CHESTPLATE -> material.equals(Material.ELYTRA) || material.name().endsWith("_CHESTPLATE");
            case LEGGINGS -> material.name().endsWith("_LEGGINGS");
            case BOOTS -> material.name().endsWith("_BOOTS");
        };
    }

    public static boolean isTool(Material material) {
        return switch (material.name()) {
            case
                    "WOODEN_HOE", "WOODEN_AXE", "WOODEN_PICKAXE", "WOODEN_SHOVEL", "WOODEN_SWORD", //WOODEN
                    "STONE_HOE", "STONE_AXE", "STONE_PICKAXE", "STONE_SHOVEL", "STONE_SWORD", //STONE
                    "IRON_HOE", "IRON_AXE", "IRON_PICKAXE", "IRON_SHOVEL", "IRON_SWORD",  //IRON
                    "GOLDEN_HOE", "GOLDEN_AXE", "GOLDEN_PICKAXE", "GOLDEN_SHOVEL", "GOLDEN_SWORD", //GOLDEN
                    "DIAMOND_HOE", "DIAMOND_AXE", "DIAMOND_PICKAXE", "DIAMOND_SHOVEL", "DIAMOND_SWORD", //DIAMOND
                    "NETHERITE_HOE", "NETHERITE_AXE", "NETHERITE_PICKAXE", "NETHERITE_SHOVEL", "NETHERITE_SWORD" //NETHERITE
                    -> true;
            default
                    -> false;
        };
    }

    public static boolean isAllowedInGrindStone(Material material) {
        var equipmentSlot = material.getEquipmentSlot();
        if (!equipmentSlot.equals(EquipmentSlot.HAND) && !equipmentSlot.equals(EquipmentSlot.OFF_HAND)) {
            return true;
        }
        if (isTool(material)) return true;
        return switch (material.name()) {
            case "BOW", "CROSSBOW", "TRIDENT", "SHIELD", "TURTLE_HELMET", "ELYTRA", "CARROT_ON_A_STICK", "WARPED_FUNGUS_ON_A_STICK", "FISHING_ROD", "SHEARS", "FLINT_AND_STEEL", "ENCHANTED_BOOK" -> true;
            default -> false;
        };
    }

    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    public static boolean isAirOrNull(CustomItem item) {
        return item == null || item.getApiReference() == null || isAirOrNull(item.getItemStack());
    }

    /*
    Prepare and configure the ItemStack for the GUI!
     */
    public static ItemStack createItem(ItemStack itemStack, String displayName, String... lore) {
        var itemBuilder = new ItemBuilder(itemStack);
        var itemMeta = itemBuilder.getItemMeta();
        if (itemMeta != null) {
            itemBuilder.setDisplayName(ChatColor.convert(displayName));
            if (lore != null) {
                for (String s : lore) {
                    itemBuilder.addLoreLine(ChatColor.convert(s));
                }
            }
            itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
        }
        return itemBuilder.create();
    }

    public static ItemStack createItem(ItemStack itemStack, Component displayName, List<Component> lore) {
        var itemBuilder = new ItemBuilder(itemStack);
        var itemMeta = itemBuilder.getItemMeta();
        if (itemMeta != null) {
            itemBuilder.setDisplayName(BukkitComponentSerializer.legacy().serialize(displayName));
            itemBuilder.setLore(lore.stream().map(line -> BukkitComponentSerializer.legacy().serialize(line)).toList());
            itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
        }
        return itemBuilder.create();
    }

    public static ItemStack applyNameAndLore(ItemStack itemStack, Component displayName, List<Component> lore) {
        var itemBuilder = new ItemBuilder(itemStack);
        var itemMeta = itemBuilder.getItemMeta();
        if (itemMeta != null) {
            itemBuilder.setDisplayName(BukkitComponentSerializer.legacy().serialize(displayName));
            itemBuilder.setLore(lore.stream().map(line -> BukkitComponentSerializer.legacy().serialize(line)).toList());
        }
        return itemBuilder.create();
    }

    public static ItemStack replaceNameAndLore(MiniMessage miniMessage, ItemStack itemStack, @NotNull TagResolver... tagResolvers) {
        var itemMeta = itemStack.getItemMeta();
        return itemMeta != null ? applyNameAndLore(itemStack, miniMessage.deserialize(itemMeta.getDisplayName(), tagResolvers), itemMeta.hasLore() ? itemMeta.getLore().stream().map(s -> miniMessage.deserialize(s, tagResolvers)).toList() : new LinkedList<>()) : itemStack;
    }

}
