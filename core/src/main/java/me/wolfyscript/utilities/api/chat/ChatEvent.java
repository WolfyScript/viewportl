package me.wolfyscript.utilities.api.chat;

public interface ChatEvent<A, V> {

    /*
    A defines the type of the value. String or BaseComponent

    V defines the type of the Action. HoverEvent.Action or ClickEvent.Action
     */

    V getValue();

    A getAction();

}
