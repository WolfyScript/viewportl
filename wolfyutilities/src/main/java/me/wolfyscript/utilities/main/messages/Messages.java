package me.wolfyscript.utilities.main.messages;

import me.wolfyscript.utilities.util.NamespacedKey;

public interface Messages {

    String KEY = "wolfyutils";
    NamespacedKey CONNECT_REQUEST = new NamespacedKey(KEY, "connection/request");
    NamespacedKey CONNECT_INFO = new NamespacedKey(KEY, "connection/info");


}
