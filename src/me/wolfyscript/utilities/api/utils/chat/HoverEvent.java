package me.wolfyscript.utilities.api.utils.chat;

import me.wolfyscript.utilities.api.utils.ItemUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

public class HoverEvent implements ChatEvent<net.md_5.bungee.api.chat.HoverEvent.Action, BaseComponent[]> {

    private net.md_5.bungee.api.chat.HoverEvent.Action action;
    private BaseComponent[] value;

    public HoverEvent(Action action, BaseComponent[] value) {
        this.action = action.getAction();
        this.value = value;
    }

    public HoverEvent(Action action, String value){
        this.action = action.getAction();
        this.value = new ComponentBuilder(value).create();
    }

    public HoverEvent(ItemStack itemStack) {
        this(Action.SHOW_ITEM, new ComponentBuilder(ItemUtils.convertItemStackToJson(itemStack)).create());
    }

    @Override
    public BaseComponent[] getValue() {
        return value;
    }

    @Override
    public net.md_5.bungee.api.chat.HoverEvent.Action getAction() {
        return action;
    }

    public enum Action{
        SHOW_TEXT(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM),
        SHOW_ACHIEVEMENT(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ACHIEVEMENT),
        SHOW_ITEM(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM),
        SHOW_ENTITY(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ENTITY);

        private net.md_5.bungee.api.chat.HoverEvent.Action action;

        Action(net.md_5.bungee.api.chat.HoverEvent.Action action){
            this.action = action;
        }

        public net.md_5.bungee.api.chat.HoverEvent.Action getAction() {
            return action;
        }
    }
}
