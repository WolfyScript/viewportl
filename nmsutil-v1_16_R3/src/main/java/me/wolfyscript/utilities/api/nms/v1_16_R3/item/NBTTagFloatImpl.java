package me.wolfyscript.utilities.api.nms.v1_16_R3.item;


import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagFloat;

public class NBTTagFloatImpl extends NBTNumberImpl<NBTTagFloat> implements me.wolfyscript.utilities.api.nms.item.NBTTagFloat {

    public static final NBTTagType<me.wolfyscript.utilities.api.nms.item.NBTTagFloat> TYPE = new NBTTagTypeImpl<me.wolfyscript.utilities.api.nms.item.NBTTagFloat>() {

        @Override
        public me.wolfyscript.utilities.api.nms.item.NBTTagFloat of(Object nbtBase) {
            return nbtBase instanceof me.wolfyscript.utilities.api.nms.item.NBTTagFloat ? new NBTTagFloatImpl((NBTTagFloat) nbtBase) : null;
        }

    };

    private NBTTagFloatImpl() {
        super(NBTTagFloat.a(0f));
    }

    NBTTagFloatImpl(NBTTagFloat nbtBase) {
        super(nbtBase);
    }

    @Override
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
