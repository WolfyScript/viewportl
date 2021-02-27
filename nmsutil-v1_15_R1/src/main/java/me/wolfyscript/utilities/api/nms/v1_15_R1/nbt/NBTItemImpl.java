package me.wolfyscript.utilities.api.nms.v1_15_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTCompound;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class NBTItemImpl extends NBTItem {

    final NBTTagCompoundImpl compound;
    private final net.minecraft.server.v1_15_R1.ItemStack nms;

    public NBTItemImpl(ItemStack bukkitItemStack) {
        super(bukkitItemStack);
        this.nms = CraftItemStack.asNMSCopy(bukkitItemStack);
        this.compound = new NBTTagCompoundImpl(this.nms.hasTag() ? this.nms.getTag() : new NBTTagCompound());
    }

    @Override
    public ItemStack create() {
        nms.setTag(compound.nbt);
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public void setTag(String key, NBTBase nbtBase) {
        compound.set(key, nbtBase);
    }

    @Override
    public @Nullable NBTBase getTag(String key) {
        return compound.get(key);
    }

    @Override
    public NBTCompound getCompound(String key) {
        return new NBTTagCompoundImpl(this.compound, key);
    }

    @Override
    public NBTCompound getCompound() {
        return this.compound;
    }

    @Override
    public boolean hasKey(String key) {
        return compound.hasKey(key);
    }

    @Override
    public boolean hasKeyOfType(String key, int typeId) {
        return compound.hasKeyOfType(key, typeId);
    }

    @Override
    public Set<String> getKeys() {
        return compound.getKeys();
    }


}
