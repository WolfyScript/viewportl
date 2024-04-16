package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTBase;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTCompound;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagList;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.ListTag;

public class NBTTagListImpl extends NBTBaseImpl<ListTag> implements NBTTagList {

    public static final NBTTagType<NBTTagList> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LIST, nbtBase -> new NBTTagListImpl((ListTag) nbtBase));

    NBTTagListImpl(ListTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagList of() {
        return new NBTTagListImpl(new ListTag());
    }

    @Override
    public NBTTagType<NBTTagList> getType() {
        return TYPE;
    }

    @Override
    public NBTCompound getCompound(int index) {
        return new NBTTagCompoundImpl(nbt.getCompound(index));
    }

    @Override
    public NBTTagList getTagList(int index) {
        return new NBTTagListImpl(nbt.getList(index));
    }

    @Override
    public NBTBase getTag(int index) {
        return NBTTagTypes.convert(nbt.get(index));
    }

    @Override
    public NBTBase remove(int index) {
        return NBTTagTypes.convert(nbt.remove(index));
    }

    @Override
    public short getShort(int index) {
        return nbt.getShort(index);
    }

    @Override
    public int getInt(int index) {
        return nbt.getInt(index);
    }

    @Override
    public int[] getIntArray(int index) {
        return nbt.getIntArray(index);
    }

    @Override
    public double getDouble(int index) {
        return nbt.getDouble(index);
    }

    @Override
    public float getFloat(int index) {
        return nbt.getFloat(index);
    }

    @Override
    public String getString(int index) {
        return nbt.getString(index);
    }

    @Override
    public int size() {
        return nbt.size();
    }

    @Override
    public NBTBase set(int index, NBTBase value) {
        return NBTTagTypes.convert(nbt.set(index, ((NBTBaseImpl<?>) value).nbt));
    }

    @Override
    public void add(int index, NBTBase value) {
        nbt.add(index, ((NBTBaseImpl<?>) value).nbt);
    }

    @Override
    public void clear() {
        nbt.clear();
    }
}
