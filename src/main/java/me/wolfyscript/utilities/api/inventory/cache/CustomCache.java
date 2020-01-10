package me.wolfyscript.utilities.api.inventory.cache;

import me.wolfyscript.utilities.api.inventory.GuiWindow;

import java.util.TreeMap;

public class CustomCache extends TreeMap<String, Object> {

    public CustomCache(){
        put("windowCache", new TreeMap<String, Object>());
    }

    public TreeMap<String, Object> getWindowCache(GuiWindow guiWindow){
        if(!hasWindowCache(guiWindow)){
            setWindowCache(guiWindow, new TreeMap<>());
            setButtons(guiWindow, new TreeMap<>());
        }
        return (TreeMap<String, Object>) ((TreeMap<String, Object>) get("windowCache")).get(guiWindow.getID());
    }

    public boolean hasWindowCache(GuiWindow guiWindow){
        return ((TreeMap<String, Object>) get("windowCache")).containsKey(guiWindow.getID());
    }

    private void setWindowCache(GuiWindow guiWindow, TreeMap<String, Object> cache){
        ((TreeMap<String, Object>) get("windowCache")).put(guiWindow.getID(), cache);
    }

    public TreeMap<Integer, String> getButtons(GuiWindow guiWindow){
        return ((TreeMap<Integer, String>) getWindowCache(guiWindow).getOrDefault("buttons", new TreeMap<Integer, String>()));
    }

    public void setButtons(GuiWindow guiWindow, TreeMap<Integer, String> buttons){
        getWindowCache(guiWindow).put("buttons", buttons);
    }
}
