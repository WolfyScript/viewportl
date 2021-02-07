package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagByte;

public class NBTTagByteImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagByte> implements NBTTagByte {

    public static final NBTTagType<NBTTagByte> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagByte ? new NBTTagByteImpl((net.minecraft.server.v1_16_R3.NBTTagByte) nbtBase) : null;

    private NBTTagByteImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagByte.b);
    }

    NBTTagByteImpl(net.minecraft.server.v1_16_R3.NBTTagByte nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByte of(byte value) {
        return new NBTTagByteImpl(net.minecraft.server.v1_16_R3.NBTTagByte.a(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
