package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByte;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.ByteTag;

public class NBTTagByteImpl extends NBTNumberImpl<ByteTag> implements NBTTagByte {

    public static final NBTTagType<NBTTagByte> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE, nbtBase -> new NBTTagByteImpl((ByteTag) nbtBase));

    private NBTTagByteImpl() {
        super(ByteTag.ZERO);
    }

    NBTTagByteImpl(ByteTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByte of(byte value) {
        return new NBTTagByteImpl(ByteTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagByte> getType() {
        return TYPE;
    }
}
