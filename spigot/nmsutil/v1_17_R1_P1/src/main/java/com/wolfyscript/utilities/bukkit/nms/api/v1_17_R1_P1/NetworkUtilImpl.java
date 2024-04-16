package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1;

import com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1.network.MCByteBufImpl;
import io.netty.buffer.ByteBuf;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NetworkUtil;
import com.wolfyscript.utilities.bukkit.nms.api.network.MCByteBuf;

public class NetworkUtilImpl extends NetworkUtil {

    protected NetworkUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public MCByteBuf buffer(ByteBuf byteBuf) {
        return new MCByteBufImpl(byteBuf);
    }

    @Override
    public MCByteBuf buffer() {
        return new MCByteBufImpl();
    }
}
