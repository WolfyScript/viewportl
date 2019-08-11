package me.wolfyscript.utilities.api.utils.chat;

public class ClickEvent implements ChatEvent<net.md_5.bungee.api.chat.ClickEvent.Action, String> {

    private net.md_5.bungee.api.chat.ClickEvent.Action action;
    private String value;

    public ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public ClickEvent(Action action, String value) {
        this(action.getAction(), value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public net.md_5.bungee.api.chat.ClickEvent.Action getAction() {
        return action;
    }

    public enum Action {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE;

        public net.md_5.bungee.api.chat.ClickEvent.Action getAction() {
            return net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(this.toString());
        }
    }
}
