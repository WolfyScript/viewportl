package me.wolfyscript.utilities.main.messages;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.network.messages.MessageAPI;
import me.wolfyscript.utilities.main.WUPlugin;

public class MessageHandler {

    private final WUPlugin plugin;
    private final WolfyUtilities wolfyUtils;
    private final MessageAPI messageAPI;

    public MessageHandler(WUPlugin wuPlugin) {
        this.plugin = wuPlugin;
        this.wolfyUtils = wuPlugin.getWolfyUtilities();
        this.messageAPI = this.wolfyUtils.getMessageAPI();
        init();
    }

    public void init() {
        messageAPI.register(Messages.CONNECT_INFO);
        messageAPI.register(Messages.CONNECT_REQUEST, (player, wolfyUtils1, buf) -> {
            if (player.hasPermission("wolfyutilities.network.connect")) {
                plugin.getMessageFactory().sendWolfyUtilsInfo(player);
            }
        });

    }

}
