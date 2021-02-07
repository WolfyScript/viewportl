package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagLong;

public class NBTTagLongImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagLong> implements NBTTagLong {

    public static final NBTTagType<NBTTagLong> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagLong ? new NBTTagLongImpl((net.minecraft.server.v1_16_R3.NBTTagLong) nbtBase) : null;

    private NBTTagLongImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagLong.a(0));
    }

    NBTTagLongImpl(net.minecraft.server.v1_16_R3.NBTTagLong nbtBase) {
        super(nbtBase);
    }

    public static NBTTagLong of(long value) {
        return new NBTTagLongImpl(net.minecraft.server.v1_16_R3.NBTTagLong.a(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
