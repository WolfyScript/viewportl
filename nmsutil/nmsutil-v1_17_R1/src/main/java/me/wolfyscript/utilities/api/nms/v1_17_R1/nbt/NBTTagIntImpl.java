package me.wolfyscript.utilities.api.nms.v1_17_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagInt;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.IntTag;

public class NBTTagIntImpl extends NBTNumberImpl<IntTag> implements NBTTagInt {

    public static final NBTTagType<NBTTagInt> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.INT, nbtBase -> new NBTTagIntImpl((IntTag) nbtBase));

    private NBTTagIntImpl() {
        super(IntTag.valueOf(0));
    }

    NBTTagIntImpl(IntTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagInt of(int value) {
        return new NBTTagIntImpl(IntTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagInt> getType() {
        return TYPE;
    }
}
