package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.inventory.button.Button;

import java.util.HashMap;

public class GuiCluster {

    private String id;
    private InventoryAPI inventoryAPI;
    private HashMap<String, Button> buttons = new HashMap<>();
    private HashMap<String, GuiWindow> guiWindows = new HashMap<>();

    private String mainmenu;

    public GuiCluster(InventoryAPI inventoryAPI){
        this.inventoryAPI = inventoryAPI;
    }

    public void setMainmenu(String guiWindowID){
        mainmenu = guiWindowID;
    }

    public String getMainMenu(){
        return mainmenu;
    }

    public void registerButton(Button button){
        buttons.putIfAbsent(button.getId(), button);
    }

    public Button getButton(String id){
        return buttons.get(id);
    }

    public void registerGuiWindow(GuiWindow guiWindow){
        guiWindow.setClusterID(id);
        guiWindow.onInit();
        guiWindows.put(guiWindow.getNamespace(), guiWindow);
    }

    public GuiWindow getGuiWindow(String id){
        return guiWindows.get(id);
    }

    void setId(String id){
        this.id = id;
    }
}
