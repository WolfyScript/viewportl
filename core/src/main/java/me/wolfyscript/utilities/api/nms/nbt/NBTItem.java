package me.wolfyscript.utilities.api.nms.nbt;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class NBTItem {

    protected final ItemStack bukkitItemStack;

    protected NBTItem(ItemStack bukkitItemStack, boolean directAccess) {
        this.bukkitItemStack = bukkitItemStack;
    }

    /**
     * @return The original item that this NBTItem was created from.
     */
    public ItemStack getItemStack() {
        return bukkitItemStack;
    }

    /**
     * Creates a new Bukkit ItemStack with the updated NBT Tags
     *
     * @return new ItemsStack instance with new NBT tags added.
     */
    public abstract ItemStack create();

    /**
     * Sets the tag with the specific key to the new value.
     *
     * @param key     The key of the tag
     * @param nbtBase The NBT value.
     */
    public abstract void setTag(String key, NBTBase nbtBase);

    @Nullable
    public abstract NBTBase getTag(String key);

    public abstract NBTCompound getCompound(String key);

    public abstract NBTCompound getCompound();

    public abstract boolean hasKey(String key);

    public abstract boolean hasKeyOfType(String key, int typeId);

    public abstract Set<String> getKeys();


}
