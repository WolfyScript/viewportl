package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagLongArray;

public class NBTTagLongArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagLongArray> implements NBTTagLongArray {

    public static final NBTTagType<NBTTagLongArray> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagLongArray ? new NBTTagLongArrayImpl((net.minecraft.server.v1_16_R3.NBTTagLongArray) nbtBase) : null;

    NBTTagLongArrayImpl(net.minecraft.server.v1_16_R3.NBTTagLongArray nbtBase) {
        super(nbtBase);
    }

    public static NBTTagLongArray of(long[] array) {
        return new NBTTagLongArrayImpl(new net.minecraft.server.v1_16_R3.NBTTagLongArray(array));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
