package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagDouble;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagDoubleImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagDouble> implements NBTTagDouble {

    public static final NBTTagType<NBTTagDouble> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.DOUBLE, nbtBase -> new NBTTagDoubleImpl((net.minecraft.server.v1_16_R3.NBTTagDouble) nbtBase));

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
    public NBTTagType<NBTTagDouble> getType() {
        return TYPE;
    }
}
