package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagFloat;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.FloatTag;

public class NBTTagFloatImpl extends NBTNumberImpl<FloatTag> implements NBTTagFloat {

    public static final NBTTagType<NBTTagFloat> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.FLOAT, nbtBase -> new NBTTagFloatImpl((FloatTag) nbtBase));

    private NBTTagFloatImpl() {
        super(FloatTag.valueOf(0f));
    }

    NBTTagFloatImpl(FloatTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagFloat of(float value) {
        return new NBTTagFloatImpl(FloatTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagFloat> getType() {
        return TYPE;
    }
}
