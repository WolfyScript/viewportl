package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagByteArray;

public class NBTTagByteArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagByteArray> implements NBTTagByteArray {

    public static final NBTTagType<NBTTagByteArray> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagByteArray ? new NBTTagByteArrayImpl((net.minecraft.server.v1_16_R3.NBTTagByteArray) nbtBase) : null;

    NBTTagByteArrayImpl(net.minecraft.server.v1_16_R3.NBTTagByteArray nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByteArray of(byte[] value) {
        return new NBTTagByteArrayImpl(new net.minecraft.server.v1_16_R3.NBTTagByteArray(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
