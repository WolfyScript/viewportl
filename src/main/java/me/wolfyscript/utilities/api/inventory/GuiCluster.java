package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;

import java.util.HashMap;

public class GuiCluster {

    private String id;
    private HashMap<String, Button> buttons;
    private HashMap<String, GuiWindow> guiWindows;

    private String mainmenu;

    public GuiCluster(){
        this.buttons = new HashMap<>();
        this.guiWindows = new HashMap<>();
        this.mainmenu = "";
    }

    public void setMainmenu(String guiWindowID){
        mainmenu = guiWindowID;
    }

    public String getMainMenu(){
        return mainmenu;
    }

    public void registerButton(Button button, WolfyUtilities api){
        button.init(id, api);
        buttons.putIfAbsent(button.getId(), button);
    }

    public Button getButton(String id){
        return buttons.get(id);
    }

    public void registerGuiWindow(GuiWindow guiWindow){
        if(this.mainmenu.isEmpty()){
            this.mainmenu = guiWindow.getNamespace();
        }
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
