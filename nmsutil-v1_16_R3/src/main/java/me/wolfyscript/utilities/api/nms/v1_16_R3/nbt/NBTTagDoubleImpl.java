package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagDouble;

public class NBTTagDoubleImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagDouble> implements NBTTagDouble {

    public static final NBTTagType<NBTTagDouble> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagDouble ? new NBTTagDoubleImpl((net.minecraft.server.v1_16_R3.NBTTagDouble) nbtBase) : null;

    private NBTTagDoubleImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagDouble.a(0d));
    }

    NBTTagDoubleImpl(net.minecraft.server.v1_16_R3.NBTTagDouble nbtBase) {
        super(nbtBase);
    }

    public static NBTTagDouble of(double value) {
        return new NBTTagDoubleImpl(net.minecraft.server.v1_16_R3.NBTTagDouble.a(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
