package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.inventory.gui.events.GuiItemDragEvent;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class InventoryAPI<T extends CustomCache> implements Listener {

    private final Plugin plugin;
    private final WolfyUtilities wolfyUtilities;
    private final HashMap<UUID, GuiHandler<T>> guiHandlers = new HashMap<>();
    private final HashMap<String, GuiCluster> guiClusters = new HashMap<>();

    private final Class<T> customCacheClass;

    public InventoryAPI(Plugin plugin, WolfyUtilities wolfyUtilities, Class<T> customCacheClass) {
        this.wolfyUtilities = wolfyUtilities;
        this.plugin = plugin;
        this.customCacheClass = customCacheClass;
        try {
            customCacheClass.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerGuiCluster(String id) {
        GuiCluster guiCluster = new GuiCluster();
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

    @Nonnull
    public GuiHandler<T> getGuiHandler(Player player) {
        if (!hasGuiHandler(player)) {
            createGuiHandler(player);
        }
        return guiHandlers.get(player.getUniqueId());
    }

    private void createGuiHandler(Player player) {
        GuiHandler<T> guiHandler = new GuiHandler<>(player, wolfyUtilities, customCacheClass, craftCustomCache());
        setPlayerGuiStudio(player, guiHandler);
    }

    private void setPlayerGuiStudio(Player player, GuiHandler<T> guiStudio) {
        guiHandlers.put(player.getUniqueId(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player, GuiHandler<?> guiStudio) {
        guiHandlers.remove(player.getUniqueId(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player) {
        guiHandlers.remove(player.getUniqueId());
    }

    public boolean hasGuiHandler(Player player) {
        return guiHandlers.containsKey(player.getUniqueId()) && guiHandlers.get(player.getUniqueId()) != null;
    }

    public boolean hasGuiHandlerAndInv(Player player) {
        return guiHandlers.containsKey(player.getUniqueId()) && guiHandlers.get(player.getUniqueId()) != null && guiHandlers.get(player.getUniqueId()).getCurrentInv() != null;
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
        guiClusters.forEach((s, guiCluster) -> {
            guiCluster.getButtons().clear();
            guiCluster.getGuiWindows().forEach((s1, guiWindow) -> {
                guiWindow.getButtons().clear();
            });
        });
    }

    @Deprecated
    public T craftCustomCache() {
        return getNewCacheInstance();
    }

    public T getNewCacheInstance() {
        try {
            return this.customCacheClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    Registers an Button globally which then can be accessed in every GUI.
     */
    public void registerButton(String clusterID, Button button) {
        getGuiCluster(clusterID).registerButton(button, getWolfyUtilities());
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
            if (hasGuiHandler((Player) event.getWhoClicked())) {
                GuiHandler<T> guiHandler = getGuiHandler((Player) event.getWhoClicked());

                if (guiHandler.verifyInventory(event.getView().getTopInventory())) {
                    GuiWindow guiWindow = guiHandler.getCurrentInv();
                    //Debug Messages
                    /*
                    System.out.println("Clicked in " + guiWindow);
                    System.out.println("    Inv Slot: " + event.getSlot());
                    System.out.println("    Inv Action: " + event.getAction());
                    System.out.println("    Click : " + event.getClick());
                    System.out.println("    Item : " + event.getCurrentItem());
                    System.out.println("    Cursor : " + event.getCursor());
                    //*/
                    event.setCancelled(true);
                    if (guiWindow == null) return;

                    List<Pair<Integer, Button>> buttons = new ArrayList<>();
                    if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                        for (Map.Entry<Integer, String> buttonEntry : guiHandler.getCustomCache().getButtons(guiWindow).entrySet()) {
                            if (buttonEntry.getKey() != event.getSlot()) {
                                Button button = guiWindow.getButton(buttonEntry.getValue());
                                if (button instanceof ItemInputButton) {
                                    buttons.add(new Pair<>(buttonEntry.getKey(), button));
                                    event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), buttonEntry.getKey(), event));
                                }
                            }
                        }
                        return;
                    }
                    if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                        Button button = guiHandler.getButton(guiWindow, event.getSlot());
                        if (button != null) {
                            buttons.add(new Pair<>(event.getSlot(), button));
                            event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), event.getSlot(), event));
                        }
                    } else {
                        event.setCancelled(false);
                        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                            int slot = -1;
                            if (event.getCurrentItem() != null) {
                                slot = InventoryUtils.firstSimilar(event.getView().getTopInventory(), event.getCurrentItem());
                            }
                            if (slot == -1) {
                                slot = event.getView().getTopInventory().firstEmpty();
                            }
                            Button button = guiHandler.getButton(guiWindow, slot);
                            if (button == null) {
                                event.setCancelled(true);
                                return;
                            }
                            event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), slot, event));
                        }
                    }
                    if (guiHandler.getCurrentInv() != null) {
                        Bukkit.getScheduler().runTask(guiHandler.getApi().getPlugin(), () -> guiHandler.getCurrentInv().update(guiHandler, buttons, event, false));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrag(InventoryDragEvent event) {
        if (hasGuiHandler((Player) event.getWhoClicked())) {
            GuiHandler<?> guiHandler = getGuiHandler((Player) event.getWhoClicked());
            if (guiHandler.verifyInventory(event.getView().getTopInventory())) {
                if (event.getRawSlots().parallelStream().anyMatch(rawSlot -> !guiHandler.verifyInventory(event.getView().getInventory(rawSlot)))) {
                    event.setCancelled(true);
                    return;
                }
                GuiWindow guiWindow = guiHandler.getCurrentInv();
                /*
                System.out.println("Clicked in "+guiWindow);
                System.out.println("    Slots: "+event.getInventorySlots());
                System.out.println("    Type: "+event.getType());
                //*/
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
                List<Pair<Integer, Button>> btns = new ArrayList<>();
                for (Map.Entry<Button, Integer> button : buttons.entrySet()) {
                    btns.add(new Pair<>(button.getValue(), button.getKey()));
                    event.setCancelled(executeButton(button.getKey(), guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), button.getValue(), new InventoryClickEvent(event.getView(), event.getView().getSlotType(button.getValue()), button.getValue(), ClickType.RIGHT, InventoryAction.PLACE_SOME)));
                }
                if (guiHandler.getCurrentInv() != null) {
                    Bukkit.getScheduler().runTask(guiHandler.getApi().getPlugin(), () -> {
                        //TODO
                        guiHandler.getCurrentInv().update(guiHandler, btns, null, false);
                    });
                }
            }
        }
    }

    private boolean executeButton(Button button, GuiHandler<?> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        try {
            return button.execute(guiHandler, player, inventory, slot, event);
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    /*
    Checks if the player sending the message has active chat events. If he has, it's executed!
    It cancels the event and parses the message into the /wui command.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreChat(AsyncPlayerChatEvent event) {
        if (hasGuiHandler(event.getPlayer())) {
            GuiHandler<?> guiHandler = getGuiHandler(event.getPlayer());
            if (guiHandler.isChatEventActive()) {
                final String message = event.getMessage();
                //Wraps normal written message into command to be executed
                Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wui" + ' ' + getPlugin().getName() + ' ' + event.getPlayer().getUniqueId().toString() + ' ' + message));
                event.setCancelled(true);
            }
        }
    }

}
