package me.wolfyscript.utilities.api.nms.v1_16_R2.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagTypes {

    private static final NBTTagType<?>[] types = new NBTTagType<?>[]{
            NBTTagEndImpl.TYPE,
            NBTTagByteImpl.TYPE,
            NBTTagShortImpl.TYPE,
            NBTTagIntImpl.TYPE,
            NBTTagLongImpl.TYPE,
            NBTTagFloatImpl.TYPE,
            NBTTagDoubleImpl.TYPE,
            NBTTagByteArrayImpl.TYPE,
            NBTTagStringImpl.TYPE,
            NBTTagListImpl.TYPE,
            NBTTagCompoundImpl.TYPE,
            NBTTagIntArrayImpl.TYPE,
            NBTTagLongArrayImpl.TYPE
    };

    public static NBTTagType<?> of(int typeId) {
        return typeId >= 0 && typeId < types.length ? types[typeId] : NBTTagType.invalidType(typeId);
    }

    public static NBTBase convert(net.minecraft.server.v1_16_R2.NBTBase base) {
        return base != null ? NBTTagTypes.of(base.getTypeId()).get(base) : null;
    }
}
