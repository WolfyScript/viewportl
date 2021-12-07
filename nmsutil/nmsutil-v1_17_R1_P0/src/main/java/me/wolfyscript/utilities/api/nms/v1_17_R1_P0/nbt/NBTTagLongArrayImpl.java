package me.wolfyscript.utilities.api.nms.v1_17_R1_P0.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagLongArray;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.LongArrayTag;

public class NBTTagLongArrayImpl extends NBTBaseImpl<LongArrayTag> implements NBTTagLongArray {

    public static final NBTTagType<NBTTagLongArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LONG_ARRAY, nbtBase -> new NBTTagLongArrayImpl((LongArrayTag) nbtBase));

    NBTTagLongArrayImpl(LongArrayTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagLongArray of(long[] array) {
        return new NBTTagLongArrayImpl(new LongArrayTag(array));
    }

    @Override
    public NBTTagType<NBTTagLongArray> getType() {
        return TYPE;
    }
}
