package me.wolfyscript.utilities.main.messages;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.entity.Player;

public class MessageFactory {

    private final WUPlugin plugin;
    private final WolfyUtilities wolfyUtils;

    public MessageFactory(WUPlugin wuPlugin) {
        this.plugin = wuPlugin;
        this.wolfyUtils = wuPlugin.getWolfyUtilities();
    }

    public void sendWolfyUtilsInfo(Player player) {
        MCByteBuf buf = wolfyUtils.getNmsUtil().getNetworkUtil().buffer();
        buf.writeBoolean(true);
        buf.writeUtf(plugin.getDescription().getVersion());
        wolfyUtils.getMessageAPI().send(Messages.CONNECT_INFO, player, buf);
    }

}
