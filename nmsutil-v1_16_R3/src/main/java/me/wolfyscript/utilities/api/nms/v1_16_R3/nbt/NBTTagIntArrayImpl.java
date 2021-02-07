package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagIntArray;

public class NBTTagIntArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagIntArray> implements NBTTagIntArray {

    public static final NBTTagType<NBTTagIntArray> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagIntArray ? new NBTTagIntArrayImpl((net.minecraft.server.v1_16_R3.NBTTagIntArray) nbtBase) : null;

    NBTTagIntArrayImpl(net.minecraft.server.v1_16_R3.NBTTagIntArray nbtBase) {
        super(nbtBase);
    }

    public static NBTTagIntArray of(int[] array) {
        return new NBTTagIntArrayImpl(new net.minecraft.server.v1_16_R3.NBTTagIntArray(array));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
