package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTCompound;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import me.wolfyscript.utilities.util.Reflection;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Set;

public class NBTItemImpl extends NBTItem {

    static final Field HANDLE_FIELD = Reflection.getDeclaredField(CraftItemStack.class, "handle");

    final NBTTagCompoundImpl compound;
    private final net.minecraft.server.v1_16_R3.ItemStack nms;

    public NBTItemImpl(ItemStack bukkitItemStack, boolean direct) {
        super(bukkitItemStack, direct);
        net.minecraft.server.v1_16_R3.ItemStack mcItemStack = null;
        if (direct && HANDLE_FIELD != null && HANDLE_FIELD.trySetAccessible()) {
            var craftItemStack = (CraftItemStack) bukkitItemStack;
            try {
                if (HANDLE_FIELD.get(craftItemStack) instanceof net.minecraft.server.v1_16_R3.ItemStack handle) {
                    mcItemStack = handle;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.nms = mcItemStack == null ? CraftItemStack.asNMSCopy(bukkitItemStack) : mcItemStack;
        this.compound = new NBTTagCompoundImpl(this.nms.hasTag() ? this.nms.getTag() : new NBTTagCompound());
        this.nms.setTag(compound.nbt);
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
