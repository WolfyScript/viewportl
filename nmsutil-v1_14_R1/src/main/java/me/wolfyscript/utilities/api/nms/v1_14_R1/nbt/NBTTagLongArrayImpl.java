package me.wolfyscript.utilities.api.nms.v1_14_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagLongArray;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagLongArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_14_R1.NBTTagLongArray> implements NBTTagLongArray {

    public static final NBTTagType<NBTTagLongArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LONG_ARRAY, nbtBase -> new NBTTagLongArrayImpl((net.minecraft.server.v1_14_R1.NBTTagLongArray) nbtBase));

    NBTTagLongArrayImpl(net.minecraft.server.v1_14_R1.NBTTagLongArray nbtBase) {
        super(nbtBase);
    }

    public static NBTTagLongArray of(long[] array) {
        return new NBTTagLongArrayImpl(new net.minecraft.server.v1_14_R1.NBTTagLongArray(array));
    }

    @Override
    public NBTTagType<NBTTagLongArray> getType() {
        return TYPE;
    }
}
