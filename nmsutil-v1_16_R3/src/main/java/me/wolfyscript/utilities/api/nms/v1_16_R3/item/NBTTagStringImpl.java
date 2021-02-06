package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagString;

public class NBTTagStringImpl extends NBTBaseImpl<NBTTagString> implements me.wolfyscript.utilities.api.nms.item.NBTTagString {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagString> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagString>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagString of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagString ? new NBTTagStringImpl((NBTTagString) nbtBase) : null;
        }

    };

    NBTTagStringImpl(NBTTagString nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
