package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagFloat;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagFloatImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagFloat> implements NBTTagFloat {

    public static final NBTTagType<NBTTagFloat> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.FLOAT, nbtBase -> new NBTTagFloatImpl((net.minecraft.server.v1_16_R3.NBTTagFloat) nbtBase));

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
    public NBTTagType<NBTTagFloat> getType() {
        return TYPE;
    }
}
