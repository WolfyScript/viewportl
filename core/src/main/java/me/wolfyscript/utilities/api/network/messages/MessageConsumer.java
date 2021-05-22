package me.wolfyscript.utilities.api.network.messages;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import org.bukkit.entity.Player;

public interface MessageConsumer {

    void accept(Player player, WolfyUtilities wolfyUtils, MCByteBuf buf);
}
