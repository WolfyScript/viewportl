package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagInt;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
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
