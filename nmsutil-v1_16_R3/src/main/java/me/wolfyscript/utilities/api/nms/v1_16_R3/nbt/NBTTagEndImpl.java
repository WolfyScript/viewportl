package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagEnd;

public class NBTTagEndImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagEnd> implements NBTTagEnd {

    public static final NBTTagType<NBTTagEnd> TYPE = nbtBase -> new NBTTagEndImpl();

    private NBTTagEndImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagEnd.b);
    }

    NBTTagEndImpl(net.minecraft.server.v1_16_R3.NBTTagEnd nbtBase) {
        super(net.minecraft.server.v1_16_R3.NBTTagEnd.b);
    }

    public static NBTTagEnd of() {
        return new NBTTagEndImpl();
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
