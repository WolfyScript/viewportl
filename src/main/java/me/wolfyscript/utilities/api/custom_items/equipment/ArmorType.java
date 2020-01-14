package me.wolfyscript.utilities.api.custom_items.equipment;

import org.bukkit.inventory.EquipmentSlot;

public enum ArmorType {
    HELMET(39, EquipmentSlot.HEAD),
    CHESTPLATE(38, EquipmentSlot.CHEST),
    LEGGINGS(37, EquipmentSlot.LEGS),
    BOOTS(36, EquipmentSlot.FEET);

    private EquipmentSlot equipmentSlot;
    private int slot;

    ArmorType(int slot, EquipmentSlot equipmentSlot){
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
        for(ArmorType armorType : values()){
            if(armorType.getSlot()==slot) return armorType;
        }
        return null;
    }
}
