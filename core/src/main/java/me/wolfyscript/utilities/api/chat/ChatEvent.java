package me.wolfyscript.utilities.api.chat;

/**
 * @param <A> defines the type of the value. String or BaseComponent
 * @param <V> defines the type of the Action. HoverEvent.Action or ClickEvent.Action
 */
public interface ChatEvent<A, V> {

    V getValue();

    A getAction();

}
