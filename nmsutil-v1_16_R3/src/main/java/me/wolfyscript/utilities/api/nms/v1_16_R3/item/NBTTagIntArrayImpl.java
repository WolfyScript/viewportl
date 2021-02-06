package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagIntArray;

public class NBTTagIntArrayImpl extends NBTBaseImpl<NBTTagIntArray> implements me.wolfyscript.utilities.api.nms.item.NBTTagIntArray {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagIntArray> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagIntArray>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagIntArray of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagIntArray ? new NBTTagIntArrayImpl((NBTTagIntArray) nbtBase) : null;
        }

    };

    NBTTagIntArrayImpl(NBTTagIntArray nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
