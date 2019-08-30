package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class GuiCluster {

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
        guiWindows.put(guiWindow.getNamespace(), guiWindow);
    }

    public GuiWindow getGuiWindow(String id){
        return guiWindows.get(id);
    }
}
