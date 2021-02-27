package me.wolfyscript.utilities.api.nms.v1_14_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagFloat;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagFloatImpl extends NBTNumberImpl<net.minecraft.server.v1_14_R1.NBTTagFloat> implements NBTTagFloat {

    public static final NBTTagType<NBTTagFloat> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.FLOAT, nbtBase -> new NBTTagFloatImpl((net.minecraft.server.v1_14_R1.NBTTagFloat) nbtBase));

    private NBTTagFloatImpl() {
        super(new net.minecraft.server.v1_14_R1.NBTTagFloat(0f));
    }

    NBTTagFloatImpl(net.minecraft.server.v1_14_R1.NBTTagFloat nbtBase) {
        super(nbtBase);
    }

    public static NBTTagFloat of(float value) {
        return new NBTTagFloatImpl(new net.minecraft.server.v1_14_R1.NBTTagFloat(value));
    }

    @Override
    public NBTTagType<NBTTagFloat> getType() {
        return TYPE;
    }
}
