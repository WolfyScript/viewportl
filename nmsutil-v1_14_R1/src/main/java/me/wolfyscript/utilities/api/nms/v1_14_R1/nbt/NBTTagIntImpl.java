package me.wolfyscript.utilities.api.nms.v1_14_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagInt;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagIntImpl extends NBTNumberImpl<net.minecraft.server.v1_14_R1.NBTTagInt> implements NBTTagInt {

    public static final NBTTagType<NBTTagInt> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.INT, nbtBase -> new NBTTagIntImpl((net.minecraft.server.v1_14_R1.NBTTagInt) nbtBase));

    private NBTTagIntImpl() {
        super(new net.minecraft.server.v1_14_R1.NBTTagInt(0));
    }

    NBTTagIntImpl(net.minecraft.server.v1_14_R1.NBTTagInt nbtBase) {
        super(nbtBase);
    }

    public static NBTTagInt of(int value) {
        return new NBTTagIntImpl(new net.minecraft.server.v1_14_R1.NBTTagInt(value));
    }

    @Override
    public NBTTagType<NBTTagInt> getType() {
        return TYPE;
    }
}
