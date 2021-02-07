package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagInt;

public class NBTTagIntImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagInt> implements NBTTagInt {

    public static final NBTTagType<NBTTagInt> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagInt ? new NBTTagIntImpl((net.minecraft.server.v1_16_R3.NBTTagInt) nbtBase) : null;

    private NBTTagIntImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagInt.a(0));
    }

    NBTTagIntImpl(net.minecraft.server.v1_16_R3.NBTTagInt nbtBase) {
        super(nbtBase);
    }

    public static NBTTagInt of(int value) {
        return new NBTTagIntImpl(net.minecraft.server.v1_16_R3.NBTTagInt.a(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
