package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagDouble;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.DoubleTag;

public class NBTTagDoubleImpl extends NBTNumberImpl<DoubleTag> implements NBTTagDouble {

    public static final NBTTagType<NBTTagDouble> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.DOUBLE, nbtBase -> new NBTTagDoubleImpl((DoubleTag) nbtBase));

    private NBTTagDoubleImpl() {
        super(DoubleTag.valueOf(0d));
    }

    NBTTagDoubleImpl(DoubleTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagDouble of(double value) {
        return new NBTTagDoubleImpl(DoubleTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagDouble> getType() {
        return TYPE;
    }
}
