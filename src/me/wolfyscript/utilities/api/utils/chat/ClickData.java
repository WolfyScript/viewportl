package me.wolfyscript.utilities.api.utils.chat;

import net.md_5.bungee.api.chat.ClickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickData {

    private String message;
    private ClickAction clickAction;
    private List<ClickEvent> clickEvents;

    public ClickData(String message, @Nullable ClickAction clickAction, ClickEvent... clickEvents){
        this.clickAction = clickAction;
        this.message = message;
        this.clickEvents = Arrays.asList(clickEvents);
    }

    public ClickData(String message, @Nullable ClickAction clickAction){
        this.clickAction = clickAction;
        this.message = message;
        this.clickEvents = new ArrayList<>();
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public String getMessage() {
        return message;
    }

    public List<ClickEvent> getClickEvents() {
        return clickEvents;
    }
}
