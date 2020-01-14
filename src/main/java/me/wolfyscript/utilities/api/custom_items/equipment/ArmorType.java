package me.wolfyscript.utilities.api.custom_items.equipment;

import org.bukkit.inventory.EquipmentSlot;

public enum ArmorType {
    HELMET(103, EquipmentSlot.HEAD),
    CHESTPLATE(102, EquipmentSlot.CHEST),
    LEGGINGS(101, EquipmentSlot.LEGS),
    BOOTS(100, EquipmentSlot.FEET);

    private EquipmentSlot equipmentSlot;
    private int slot;

    private ArmorType(int slot, EquipmentSlot equipmentSlot){
        this.equipmentSlot = equipmentSlot;
        this.slot = slot;
    }

    public EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }

    public int getSlot() {
        return slot;
    }

    public static ArmorType getBySlot(int slot){
        
    }
}
