package me.wolfyscript.utilities.api.nms.item;

import org.bukkit.inventory.ItemStack;

public abstract class NBTItem {

    protected final ItemStack bukkitItemStack;

    public NBTItem(ItemStack bukkitItemStack) {
        this.bukkitItemStack = bukkitItemStack;
    }

    public ItemStack getItemStack() {
        return bukkitItemStack;
    }

    public abstract ItemStack create();

    public abstract NBTCompound getCompound(String key);

    public abstract boolean hasKey(String key);

    public abstract boolean hasKeyOfType(String key, int typeId);


}
