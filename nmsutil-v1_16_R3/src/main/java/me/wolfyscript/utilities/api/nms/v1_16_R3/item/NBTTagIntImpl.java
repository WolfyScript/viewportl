package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagInt;

public class NBTTagIntImpl extends NBTNumberImpl<NBTTagInt> implements me.wolfyscript.utilities.api.nms.item.NBTTagInt {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagInt> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagInt>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagInt of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagInt ? new NBTTagIntImpl((NBTTagInt) nbtBase) : null;
        }

    };

    private NBTTagIntImpl() {
        super(NBTTagInt.a(0));
    }

    NBTTagIntImpl(NBTTagInt nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
