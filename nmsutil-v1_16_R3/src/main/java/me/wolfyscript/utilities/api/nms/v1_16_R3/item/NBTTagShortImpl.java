package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagShort;

public class NBTTagShortImpl extends NBTNumberImpl<NBTTagShort> implements me.wolfyscript.utilities.api.nms.item.NBTTagShort {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagShort> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagShort>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagShort of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagShort ? new NBTTagShortImpl((NBTTagShort) nbtBase) : null;
        }

    };

    private NBTTagShortImpl() {
        super(NBTTagShort.a((short) 0));
    }

    NBTTagShortImpl(NBTTagShort nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
