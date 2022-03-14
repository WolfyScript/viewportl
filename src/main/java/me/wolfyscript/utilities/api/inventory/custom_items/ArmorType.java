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

package me.wolfyscript.utilities.api.inventory.custom_items;

import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum ArmorType {
    HELMET(39, EquipmentSlot.HEAD),
    CHESTPLATE(38, EquipmentSlot.CHEST),
    LEGGINGS(37, EquipmentSlot.LEGS),
    BOOTS(36, EquipmentSlot.FEET);

    private final EquipmentSlot equipmentSlot;
    private final int slot;

    ArmorType(int slot, EquipmentSlot equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
        this.slot = slot;
    }

    public EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }

    public int getSlot() {
        return slot;
    }

    public static ArmorType getBySlot(int slot) {
        for (ArmorType armorType : values()) {
            if (armorType.getSlot() == slot) return armorType;
        }
        return null;
    }

    public static ArmorType getBySlot(EquipmentSlot slot) {
        for (ArmorType armorType : values()) {
            if (armorType.getEquipmentSlot() == slot) return armorType;
        }
        return null;
    }

    public static ArmorType matchType(final CustomItem customItem) {
        return matchType(null, customItem, null);
    }

    public static ArmorType matchType(final CustomItem customItem, final PlayerInventory playerInventory) {
        return matchType(null, customItem, playerInventory);
    }

    public static ArmorType matchType(final ItemStack itemStack) {
        return matchType(itemStack, null, null);
    }

    public static ArmorType matchType(final ItemStack itemStack, final PlayerInventory playerInventory) {
        return matchType(itemStack, null, playerInventory);
    }

    public static ArmorType matchType(final ItemStack itemStack, final CustomItem customItem) {
        return matchType(itemStack, customItem, null);
    }

    public static ArmorType matchType(final ItemStack itemStack, final CustomItem customItem, final PlayerInventory playerInventory) {
        if (!ItemUtils.isAirOrNull(customItem) && customItem.hasEquipmentSlot()) {
            if (playerInventory == null) return getBySlot(customItem.getEquipmentSlots().get(0));
            var armorType = customItem.getEquipmentSlots().stream().map(ArmorType::getBySlot).filter(type -> ItemUtils.isAirOrNull(playerInventory.getItem(type.getSlot()))).findFirst().orElse(null);
            if (armorType != null) {
                return armorType;
            }else if(customItem.isBlockVanillaEquip()){
                return null;
            }
        }
        if(itemStack != null){
            String type = itemStack.getType().name();
            ArmorType armorType = null;
            if (type.endsWith("_HELMET") || type.endsWith("_SKULL")) armorType = HELMET;
            else if (type.endsWith("_CHESTPLATE") || type.endsWith("ELYTRA")) armorType = CHESTPLATE;
            else if (type.endsWith("_LEGGINGS")) armorType = LEGGINGS;
            else if (type.endsWith("_BOOTS")) armorType = BOOTS;
            if(armorType != null){
                if(playerInventory != null && !ItemUtils.isAirOrNull(playerInventory.getItem(armorType.getSlot()))) return null;
                return armorType;
            }
        }
        return null;
    }
}
