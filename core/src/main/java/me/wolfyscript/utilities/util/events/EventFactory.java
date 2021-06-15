package me.wolfyscript.utilities.util.events;

import me.wolfyscript.utilities.api.inventory.custom_items.ArmorType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EventFactory {

    private EventFactory() {
    }

    public static ArmorEquipEvent createArmorEquipEvent(Player player, ArmorEquipEvent.EquipMethod equipType, ArmorType type, ItemStack oldArmorPiece, ItemStack newArmorPiece) {
        var equipEvent = new ArmorEquipEvent(player, equipType, type, oldArmorPiece, newArmorPiece);
        Bukkit.getPluginManager().callEvent(equipEvent);
        return equipEvent;
    }

    public static ArmorEquipEvent createArmorEquipEvent(Player player, ArmorEquipEvent.EquipMethod equipType, ArmorType type, ItemStack oldArmorPiece, ItemStack newArmorPiece, CustomItem oldCustomArmorPiece, CustomItem newCustomArmorPiece) {
        var equipEvent = new ArmorEquipEvent(player, equipType, type, oldArmorPiece, newArmorPiece, oldCustomArmorPiece, newCustomArmorPiece);
        Bukkit.getPluginManager().callEvent(equipEvent);
        return equipEvent;
    }
}
