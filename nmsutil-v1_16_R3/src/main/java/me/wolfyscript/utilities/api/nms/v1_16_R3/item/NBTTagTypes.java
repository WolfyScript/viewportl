package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTTagType;

public class NBTTagTypes {

    private static final me.wolfyscript.utilities.api.nms.item.NBTTagType<?>[] a;

    static {
        a = new NBTTagType<?>[]{NBTTagEndImpl.TYPE, NBTTagByteImpl.TYPE, NBTTagShortImpl.TYPE, NBTTagIntImpl.TYPE, NBTTagLongImpl.TYPE, NBTTagFloatImpl.TYPE, NBTTagDoubleImpl.TYPE, NBTTagByteArrayImpl.TYPE, NBTTagStringImpl.TYPE, NBTTagListImpl.TYPE, NBTTagCompoundImpl.TYPE, NBTTagIntArrayImpl.TYPE, NBTTagLongArrayImpl.TYPE};
    }

    public static me.wolfyscript.utilities.api.nms.item.NBTTagType<?> of(int typeId) {
        return typeId >= 0 && typeId < a.length ? a[typeId] : NBTTagTypeImpl.invalidType(typeId);
    }
}
