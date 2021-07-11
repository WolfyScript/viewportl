package me.wolfyscript.utilities.api.nms.v1_17_R1_P1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagEnd;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.EndTag;

public class NBTTagEndImpl extends NBTBaseImpl<EndTag> implements NBTTagEnd {

    public static final NBTTagType<NBTTagEnd> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.TAG_END, nbtBase -> new NBTTagEndImpl());

    private NBTTagEndImpl() {
        super(EndTag.INSTANCE);
    }

    NBTTagEndImpl(EndTag nbtBase) {
        super(EndTag.INSTANCE);
    }

    public static NBTTagEnd of() {
        return new NBTTagEndImpl();
    }

    @Override
    public NBTTagType<NBTTagEnd> getType() {
        return TYPE;
    }
}
