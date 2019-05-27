package me.wolfyscript.utilities.api.utils.chat;

import me.wolfyscript.utilities.api.utils.ItemUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.inventory.ItemStack;

public class HoverEvent implements ChatEvent<net.md_5.bungee.api.chat.HoverEvent.Action, BaseComponent[]> {

    private net.md_5.bungee.api.chat.HoverEvent.Action action;
    private BaseComponent[] value;

    public HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action action, BaseComponent[] value) {
        this.action = action;
        this.value = value;
    }

    public HoverEvent(ItemStack itemStack) {
        this(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemUtils.convertItemStackToJson(itemStack)).create());
    }

    @Override
    public BaseComponent[] getValue() {
        return value;
    }

    @Override
    public net.md_5.bungee.api.chat.HoverEvent.Action getAction() {
        return action;
    }
}
