package me.wolfyscript.utilities.api.inventory;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAPI implements Listener {

    private Plugin plugin;
    private WolfyUtilities wolfyUtilities;
    private HashMap<String, GuiHandler> guiHandlers = new HashMap<>();
    private HashMap<String, GuiCluster> guiClusters = new HashMap<>();

    public InventoryAPI(Plugin plugin, WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerGuiCluster(String id) {
        GuiCluster guiCluster = new GuiCluster(this);
        guiCluster.setId(id);
        guiClusters.putIfAbsent(id, guiCluster);
    }

    public void registerCustomGuiCluster(String id, GuiCluster guiCluster) {
        guiCluster.setId(id);
        guiClusters.putIfAbsent(id, guiCluster);
    }

    public GuiCluster getOrRegisterGuiCluster(String clusterID) {
        registerGuiCluster(clusterID);
        return getGuiCluster(clusterID);
    }

    public GuiCluster getGuiCluster(String id) {
        return guiClusters.get(id);
    }

    public GuiCluster getGuiCluster() {
        return getGuiCluster("none");
    }

    public boolean hasGuiCluster(String id) {
        return getGuiCluster(id) != null;
    }

    public void registerGuiWindow(String clusterID, GuiWindow guiWindow) {
        getGuiCluster(clusterID).registerGuiWindow(guiWindow);
    }

    public void registerGuiWindow(GuiWindow guiWindow) {
        registerGuiWindow("none", guiWindow);
    }

    public GuiWindow getGuiWindow(String clusterID, String guiWindowID) {
        return getGuiCluster(clusterID).getGuiWindow(guiWindowID);
    }

    public GuiWindow getGuiWindow(String guiWindowID) {
        return getGuiCluster("none").getGuiWindow(guiWindowID);
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public void setMainmenu(String guiWindowID) {
        getGuiCluster("none").setMainmenu(guiWindowID);
    }

    public void openCluster(Player player, String clusterID) {
        getGuiHandler(player).openCluster(clusterID);
    }

    @Deprecated
    public void openGui(Player player, String guiWindowID) {
        getGuiHandler(player).changeToInv(guiWindowID);
    }

    public void openGui(Player player, String clusterID, String guiWindowID) {
        getGuiHandler(player).changeToInv(clusterID, guiWindowID);
    }

    public void removeGui(Player player) {
        if (hasGuiHandler(player)) {
            removePlayerGuiHandler(player);
        }
    }

    @NotNull
    public GuiHandler getGuiHandler(Player player) {
        if (!hasGuiHandler(player)) {
            createGuiHandler(player);
        }
        return guiHandlers.get(player.getUniqueId().toString());
    }

    private void createGuiHandler(Player player) {
        setPlayerGuiStudio(player, new GuiHandler(player, wolfyUtilities));
    }

    private void setPlayerGuiStudio(Player player, GuiHandler guiStudio) {
        guiHandlers.put(player.getUniqueId().toString(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player, GuiHandler guiStudio) {
        guiHandlers.remove(player.getUniqueId().toString(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player) {
        guiHandlers.remove(player.getUniqueId().toString());
    }

    public boolean hasGuiHandler(Player player) {
        return guiHandlers.containsKey(player.getUniqueId().toString()) && guiHandlers.get(player.getUniqueId().toString()) != null;
    }

    public boolean hasGuiHandlerAndInv(Player player) {
        return guiHandlers.containsKey(player.getUniqueId().toString()) && guiHandlers.get(player.getUniqueId().toString()) != null && guiHandlers.get(player.getUniqueId().toString()).getCurrentInv() != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void reset() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.closeInventory();
            removeGui(player);
        }
        guiHandlers.clear();
    }

    /*
    Registers an Button globally which then can be accessed in every GUI.
     */
    public void registerButton(String clusterID, Button button) {
        button.init(clusterID, getWolfyUtilities());
        getGuiCluster(clusterID).registerButton(button);
    }

    /*
    Get an globally registered Button.
    This returns an Button out of the specific namespace.
     */
    public Button getButton(String clusterID, String buttonID) {
        return getGuiCluster(clusterID).getButton(buttonID);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            System.out.println("Type: " + event.getAction());
            if (hasGuiHandler((Player) event.getWhoClicked())) {
                GuiHandler guiHandler = getGuiHandler((Player) event.getWhoClicked());
                if (guiHandler.verifyInventory(event.getView().getTopInventory())) {
                    GuiWindow guiWindow = guiHandler.getCurrentInv();
                    if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
                        event.setCancelled(true);

                        Button button = guiHandler.getButton(guiWindow, event.getSlot());
                        if (button != null) {
                            event.setCancelled(button.execute(guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), event.getSlot(), event));
                            guiHandler.getCurrentInv().update(guiHandler);
                        }
                    } else {
                        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                            int slot = -1;
                            if (event.getCurrentItem() != null) {
                                slot = event.getView().getTopInventory().first(event.getCurrentItem());
                            }
                            if (slot == -1) {
                                slot = event.getView().getTopInventory().firstEmpty();
                            }
                            Button button = guiHandler.getButton(guiWindow, slot);
                            if (button != null) {
                                event.setCancelled(button.execute(guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), slot, event));
                                guiHandler.getCurrentInv().update(guiHandler);
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                    //TODO: Double click item to execute all affecting buttons.
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrag(InventoryDragEvent event) {
        if (event.getInventory() != null) {
            if (hasGuiHandler((Player) event.getWhoClicked())) {
                GuiHandler guiHandler = getGuiHandler((Player) event.getWhoClicked());
                for (int rawSlot : event.getRawSlots()) {
                    if (!guiHandler.verifyInventory(event.getView().getInventory(rawSlot))) {
                        event.setCancelled(true);
                        return;
                    }
                }
                GuiWindow guiWindow = guiHandler.getCurrentInv();
                GuiItemDragEvent guiItemDragEvent = new GuiItemDragEvent(guiHandler, event);
                Bukkit.getPluginManager().callEvent(guiItemDragEvent);
                if (guiItemDragEvent.isCancelled()) {
                    event.setCancelled(true);
                }
                HashMap<Button, Integer> buttons = new HashMap<>();
                for (int slot : event.getInventorySlots()) {
                    Button button = guiHandler.getButton(guiWindow, slot);
                    if (button == null) {
                        event.setCancelled(true);
                        return;
                    }
                    buttons.put(button, slot);
                }
                for(Map.Entry<Button, Integer> button : buttons.entrySet()){
                    event.setCancelled(button.getKey().execute(guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), button.getValue(), new InventoryClickEvent(event.getView(), event.getView().getSlotType(button.getValue()), button.getValue(), ClickType.RIGHT, InventoryAction.PLACE_SOME)));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage() != null) {
            if (event.getMessage().equals("[WolfyUtilities CANCELED]")) {
                event.setMessage("");
                event.setCancelled(true);
            }
        }
    }

    /*
    Checks if the player sending the message has active chat events. If he has, it's executed!
    It sets the message to a canceled string, so the following event knows to cancel it.
    This allows the message to bypass other Chat Plugins.
    Maybe I find another way to do it someday...
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreChat(AsyncPlayerChatEvent event) {
        if (event.getMessage() != null) {
            if (hasGuiHandler(event.getPlayer())) {
                GuiHandler guiHandler = getGuiHandler(event.getPlayer());
                if (guiHandler.isChatEventActive() && !event.getMessage().startsWith("wu::")) {
                    if (guiHandler.getChatInputAction() != null && !guiHandler.getChatInputAction().onChat(guiHandler, event.getPlayer(), event.getMessage(), event.getMessage().split(" "))) {
                        guiHandler.setChatInputAction(null);
                        guiHandler.openCluster();
                    }
                    event.setMessage("[WolfyUtilities CANCELED]");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCancel(PlayerCommandPreprocessEvent event) {
        if (hasGuiHandler(event.getPlayer())) {
            GuiHandler guiHandler = getGuiHandler(event.getPlayer());
            if (guiHandler.isChatEventActive()) {
                guiHandler.cancelChatEvent();
            }
        }
    }

}
