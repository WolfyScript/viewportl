package me.wolfyscript.utilities.api.nms.v1_17_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagIntArray;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.IntArrayTag;

public class NBTTagIntArrayImpl extends NBTBaseImpl<IntArrayTag> implements NBTTagIntArray {

    public static final NBTTagType<NBTTagIntArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.INT_ARRAY, nbtBase -> new NBTTagIntArrayImpl((IntArrayTag) nbtBase));

    NBTTagIntArrayImpl(IntArrayTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagIntArray of(int[] array) {
        return new NBTTagIntArrayImpl(new IntArrayTag(array));
    }

    @Override
    public NBTTagType<NBTTagIntArray> getType() {
        return TYPE;
    }
}
