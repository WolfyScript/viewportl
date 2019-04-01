package me.wolfyscript.utilities.api.utils.chat;

import javax.annotation.Nullable;

public class ClickData {

    private String message;
    private ClickAction clickAction;

    public ClickData(String message, @Nullable ClickAction clickAction){
        this.clickAction = clickAction;
        this.message = message;
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public String getMessage() {
        return message;
    }
}
