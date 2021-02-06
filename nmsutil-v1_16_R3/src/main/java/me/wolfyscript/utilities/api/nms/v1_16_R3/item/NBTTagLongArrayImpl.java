package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagLongArray;

public class NBTTagLongArrayImpl extends NBTBaseImpl<NBTTagLongArray> implements me.wolfyscript.utilities.api.nms.item.NBTTagLongArray {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagLongArray> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagLongArray>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagLongArray of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagLongArray ? new NBTTagLongArrayImpl((NBTTagLongArray) nbtBase) : null;
        }

    };

    NBTTagLongArrayImpl(NBTTagLongArray nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
