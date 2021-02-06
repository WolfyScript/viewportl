package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagByte;

public class NBTTagByteImpl extends NBTNumberImpl<NBTTagByte> implements me.wolfyscript.utilities.api.nms.item.NBTTagByte {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagByte> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagByte>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagByte of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagByte ? new NBTTagByteImpl((NBTTagByte) nbtBase) : null;
        }

    };

    private NBTTagByteImpl() {
        super(NBTTagByte.a((byte) 0));
    }

    NBTTagByteImpl(NBTTagByte nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
