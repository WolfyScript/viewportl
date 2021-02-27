package me.wolfyscript.utilities.api.nms.v1_14_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTCompound;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagList;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagListImpl extends NBTBaseImpl<net.minecraft.server.v1_14_R1.NBTTagList> implements NBTTagList {

    public static final NBTTagType<NBTTagList> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LIST, nbtBase -> new NBTTagListImpl((net.minecraft.server.v1_14_R1.NBTTagList) nbtBase));

    NBTTagListImpl(net.minecraft.server.v1_14_R1.NBTTagList nbtBase) {
        super(nbtBase);
    }

    public static NBTTagList of() {
        return new NBTTagListImpl(new net.minecraft.server.v1_14_R1.NBTTagList());
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
        return new NBTTagListImpl(nbt.b(index));
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
        return nbt.d(index);
    }

    @Override
    public int getInt(int index) {
        return nbt.e(index);
    }

    @Override
    public int[] getIntArray(int index) {
        return nbt.f(index);
    }

    @Override
    public double getDouble(int index) {
        return nbt.h(index);
    }

    @Override
    public float getFloat(int index) {
        return nbt.i(index);
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
