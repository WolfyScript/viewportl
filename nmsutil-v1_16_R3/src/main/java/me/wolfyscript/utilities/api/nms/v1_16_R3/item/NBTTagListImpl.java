package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagList;

public class NBTTagListImpl extends NBTBaseImpl<NBTTagList> implements me.wolfyscript.utilities.api.nms.item.NBTTagList {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagList> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagList>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagList of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagList ? new NBTTagListImpl((NBTTagList) nbtBase) : null;
        }

    };

    NBTTagListImpl(NBTTagList nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
