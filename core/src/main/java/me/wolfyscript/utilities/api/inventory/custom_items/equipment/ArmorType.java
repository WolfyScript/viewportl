package me.wolfyscript.utilities.api.inventory.custom_items.equipment;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
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
            ArmorType armorType = customItem.getEquipmentSlots().stream().map(ArmorType::getBySlot).filter(type -> ItemUtils.isAirOrNull(playerInventory.getItem(type.getSlot()))).findFirst().orElse(null);
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
