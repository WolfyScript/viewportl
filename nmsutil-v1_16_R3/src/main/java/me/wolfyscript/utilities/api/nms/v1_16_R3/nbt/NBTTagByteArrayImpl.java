package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagByteArray;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagByteArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagByteArray> implements NBTTagByteArray {

    public static final NBTTagType<NBTTagByteArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE_ARRAY, nbtBase -> new NBTTagByteArrayImpl((net.minecraft.server.v1_16_R3.NBTTagByteArray) nbtBase));

    NBTTagByteArrayImpl(net.minecraft.server.v1_16_R3.NBTTagByteArray nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByteArray of(byte[] value) {
        return new NBTTagByteArrayImpl(new net.minecraft.server.v1_16_R3.NBTTagByteArray(value));
    }

    @Override
    public NBTTagType<NBTTagByteArray> getType() {
        return TYPE;
    }
}
