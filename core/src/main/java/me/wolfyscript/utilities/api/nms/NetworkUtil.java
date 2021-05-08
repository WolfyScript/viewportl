package me.wolfyscript.utilities.api.nms;

import io.netty.buffer.ByteBuf;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;

public abstract class NetworkUtil extends UtilComponent {

    protected NetworkUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    public abstract MCByteBuf buffer(ByteBuf byteBuf);

    public abstract MCByteBuf buffer();

}
