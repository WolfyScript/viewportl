package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagDouble;

public class NBTTagDoubleImpl extends NBTNumberImpl<NBTTagDouble> implements me.wolfyscript.utilities.api.nms.item.NBTTagDouble {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagDouble> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagDouble>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagDouble of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagDouble ? new NBTTagDoubleImpl((NBTTagDouble) nbtBase) : null;
        }

    };

    private NBTTagDoubleImpl() {
        super(NBTTagDouble.a(0d));
    }

    NBTTagDoubleImpl(NBTTagDouble nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
