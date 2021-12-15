package me.wolfyscript.utilities.api.nms.v1_16_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagByte;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagByteImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R1.NBTTagByte> implements NBTTagByte {

    public static final NBTTagType<NBTTagByte> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE, nbtBase -> new NBTTagByteImpl((net.minecraft.server.v1_16_R1.NBTTagByte) nbtBase));

    private NBTTagByteImpl() {
        super(net.minecraft.server.v1_16_R1.NBTTagByte.b);
    }

    NBTTagByteImpl(net.minecraft.server.v1_16_R1.NBTTagByte nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByte of(byte value) {
        return new NBTTagByteImpl(net.minecraft.server.v1_16_R1.NBTTagByte.a(value));
    }

    @Override
    public NBTTagType<NBTTagByte> getType() {
        return TYPE;
    }
}
