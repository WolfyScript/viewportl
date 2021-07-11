package me.wolfyscript.utilities.api.nms.v1_17_R1_P0.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagLong;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.LongTag;

public class NBTTagLongImpl extends NBTNumberImpl<LongTag> implements NBTTagLong {

    public static final NBTTagType<NBTTagLong> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LONG, nbtBase -> new NBTTagLongImpl((LongTag) nbtBase));

    private NBTTagLongImpl() {
        super(LongTag.valueOf(0));
    }

    NBTTagLongImpl(LongTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagLong of(long value) {
        return new NBTTagLongImpl(LongTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagLong> getType() {
        return TYPE;
    }
}
