package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagByteArray;
import me.wolfyscript.utilities.api.nms.item.NBTTagType;

public class NBTTagByteArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagByteArray> implements NBTTagByteArray {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagByteArray> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagByteArray>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagByteArray of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagByteArray ? new NBTTagByteArrayImpl((net.minecraft.server.v1_16_R3.NBTTagByteArray) nbtBase) : null;
        }

    };

    NBTTagByteArrayImpl(net.minecraft.server.v1_16_R3.NBTTagByteArray nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
