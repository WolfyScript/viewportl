package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagLong;

public class NBTTagLongImpl extends NBTNumberImpl<NBTTagLong> implements me.wolfyscript.utilities.api.nms.item.NBTTagLong {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagLong> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagLong>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagLong of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagLong ? new NBTTagLongImpl((NBTTagLong) nbtBase) : null;
        }

    };

    private NBTTagLongImpl() {
        super(NBTTagLong.a(0));
    }

    NBTTagLongImpl(NBTTagLong nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
