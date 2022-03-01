package me.wolfyscript.utilities.api.nms.v1_17_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagShort;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.ShortTag;

public class NBTTagShortImpl extends NBTNumberImpl<ShortTag> implements NBTTagShort {

    public static final NBTTagType<NBTTagShort> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.SHORT, nbtBase -> new NBTTagShortImpl((ShortTag) nbtBase));

    private NBTTagShortImpl() {
        super(ShortTag.valueOf((short) 0));
    }

    NBTTagShortImpl(ShortTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagShort of(short value) {
        return new NBTTagShortImpl(ShortTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagShort> getType() {
        return TYPE;
    }
}
