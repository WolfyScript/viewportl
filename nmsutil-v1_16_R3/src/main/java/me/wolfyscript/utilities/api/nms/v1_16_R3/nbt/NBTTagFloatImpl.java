package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagFloat;

public class NBTTagFloatImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagFloat> implements NBTTagFloat {

    public static final NBTTagType<NBTTagFloat> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagFloat ? new NBTTagFloatImpl((net.minecraft.server.v1_16_R3.NBTTagFloat) nbtBase) : null;

    private NBTTagFloatImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagFloat.a(0f));
    }

    NBTTagFloatImpl(net.minecraft.server.v1_16_R3.NBTTagFloat nbtBase) {
        super(nbtBase);
    }

    public static NBTTagFloat of(float value) {
        return new NBTTagFloatImpl(net.minecraft.server.v1_16_R3.NBTTagFloat.a(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
