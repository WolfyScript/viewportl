package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagEnd;
import me.wolfyscript.utilities.api.nms.item.NBTTagType;

public class NBTTagEndImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagEnd> implements me.wolfyscript.utilities.api.nms.item.NBTTagEnd {

    public static final NBTTagType<NBTTagEnd> TYPE = new NBTTagTypeImpl<NBTTagEnd>() {
        @Override
        public NBTTagEnd of(Object nbtBase) {
            return new NBTTagEndImpl();
        }

    };

    private NBTTagEndImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagEnd.b);
    }

    NBTTagEndImpl(net.minecraft.server.v1_16_R3.NBTTagEnd nbtBase) {
        super(net.minecraft.server.v1_16_R3.NBTTagEnd.b);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
