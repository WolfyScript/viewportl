package me.wolfyscript.utilities.api.nms.v1_17_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagFloat;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
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
