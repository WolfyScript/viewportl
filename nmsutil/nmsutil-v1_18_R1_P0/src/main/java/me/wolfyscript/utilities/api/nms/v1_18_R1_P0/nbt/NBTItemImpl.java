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

package me.wolfyscript.utilities.api.nms.v1_18_R1_P0.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTCompound;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import me.wolfyscript.utilities.util.Reflection;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Set;

public class NBTItemImpl extends NBTItem {

    static final Field HANDLE_FIELD = Reflection.getDeclaredField(CraftItemStack.class, "handle");

    final NBTTagCompoundImpl compound;
    private final net.minecraft.world.item.ItemStack nms;

    public NBTItemImpl(ItemStack bukkitItemStack, boolean directAccess) {
        super(bukkitItemStack, directAccess);
        net.minecraft.world.item.ItemStack mcItemStack = null;
        if (directAccess && HANDLE_FIELD != null && HANDLE_FIELD.trySetAccessible()) {
            var craftItemStack = (CraftItemStack) bukkitItemStack;
            try {
                if (HANDLE_FIELD.get(craftItemStack) instanceof net.minecraft.world.item.ItemStack handle) {
                    mcItemStack = handle;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.nms = mcItemStack == null ? CraftItemStack.asNMSCopy(bukkitItemStack) : mcItemStack;
        this.compound = new NBTTagCompoundImpl(this.nms.hasTag() ? this.nms.getTag() : new CompoundTag());
        this.nms.setTag(compound.nbt);
    }

    @Override
    public ItemStack create() {
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
