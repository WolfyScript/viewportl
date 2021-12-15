package me.wolfyscript.utilities.api.nms.v1_16_R2.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagEnd;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagEndImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R2.NBTTagEnd> implements NBTTagEnd {

    public static final NBTTagType<NBTTagEnd> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.TAG_END, nbtBase -> new NBTTagEndImpl());

    private NBTTagEndImpl() {
        super(net.minecraft.server.v1_16_R2.NBTTagEnd.b);
    }

    NBTTagEndImpl(net.minecraft.server.v1_16_R2.NBTTagEnd nbtBase) {
        super(net.minecraft.server.v1_16_R2.NBTTagEnd.b);
    }

    public static NBTTagEnd of() {
        return new NBTTagEndImpl();
    }

    @Override
    public NBTTagType<NBTTagEnd> getType() {
        return TYPE;
    }
}
