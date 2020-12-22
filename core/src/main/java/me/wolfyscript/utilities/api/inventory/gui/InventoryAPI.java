package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.inventory.gui.events.GuiItemDragEvent;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class InventoryAPI<C extends CustomCache> implements Listener {

    private final Plugin plugin;
    private final WolfyUtilities wolfyUtilities;
    private final HashMap<UUID, GuiHandler<C>> guiHandlers = new HashMap<>();
    private final HashMap<String, GuiCluster<C>> guiClusters = new HashMap<>();

    private final Class<C> customCacheClass;

    public InventoryAPI(Plugin plugin, WolfyUtilities wolfyUtilities, Class<C> customCacheClass) {
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

    public void registerCluster(GuiCluster<C> guiCluster) {
        guiCluster.onInit();
        guiClusters.putIfAbsent(guiCluster.getId(), guiCluster);
    }

    public GuiCluster<C> getGuiCluster(String clusterID) {
        return guiClusters.get(clusterID);
    }

    public boolean hasGuiCluster(String clusterID) {
        return getGuiCluster(clusterID) != null;
    }

    public GuiWindow<C> getGuiWindow(NamespacedKey namespacedKey) {
        return getGuiCluster(namespacedKey.getNamespace()).getGuiWindow(namespacedKey.getKey());
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public void openCluster(Player player, String clusterID) {
        getGuiHandler(player).openCluster(clusterID);
    }

    public void openGui(Player player, NamespacedKey namespacedKey) {
        getGuiHandler(player).openWindow(namespacedKey);
    }

    public void removeGui(Player player) {
        if (hasGuiHandler(player)) {
            removePlayerGuiHandler(player);
        }
    }

    @Nonnull
    public GuiHandler<C> getGuiHandler(Player player) {
        if (!hasGuiHandler(player)) {
            createGuiHandler(player);
        }
        return guiHandlers.get(player.getUniqueId());
    }

    private void createGuiHandler(Player player) {
        GuiHandler<C> guiHandler = new GuiHandler<>(player, wolfyUtilities, this, getNewCacheInstance());
        setPlayerGuiHandler(player, guiHandler);
    }

    private void setPlayerGuiHandler(Player player, GuiHandler<C> guiStudio) {
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

    public boolean hasGuiHandlerAndWindow(Player player) {
        return guiHandlers.containsKey(player.getUniqueId()) && guiHandlers.get(player.getUniqueId()) != null && guiHandlers.get(player.getUniqueId()).getWindow() != null;
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
            guiCluster.getGuiWindows().values().forEach(guiWindow -> guiWindow.buttons.clear());
        });
    }

    public C getNewCacheInstance() {
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
    public void registerButton(String clusterID, Button<C> button) {
        getGuiCluster(clusterID).registerButton(button);
    }

    /*
    Get an globally registered Button.
    This returns an Button out of the specific namespace.
     */
    public Button<C> getButton(String clusterID, String buttonID) {
        return getGuiCluster(clusterID).getButton(buttonID);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory instanceof GUIInventory && ((GUIInventory<?>) inventory).getGuiHandler().getInvAPI().equals(this)) {
            GUIInventory<C> guiInventory = (GUIInventory<C>) inventory;
            GuiHandler<C> guiHandler = guiInventory.getGuiHandler();
            GuiWindow<C> guiWindow = guiInventory.getWindow();
            event.setCancelled(true);
            if (guiWindow == null) return;

            HashMap<Integer, Button<C>> buttons = new HashMap<>();
            if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                for (Map.Entry<Integer, String> buttonEntry : guiHandler.getCustomCache().getButtons(guiWindow).entrySet()) {
                    if (event.getSlot() != buttonEntry.getKey()) {
                        Button<C> button = guiWindow.getButton(buttonEntry.getValue());
                        if (button instanceof ItemInputButton) {
                            buttons.put(buttonEntry.getKey(), button);
                            event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), guiInventory, buttonEntry.getKey(), event));
                        }
                    }
                }
            }
            if (inventory.equals(event.getClickedInventory())) {
                Button<C> button = guiHandler.getButton(guiWindow, event.getSlot());
                if (button != null) {
                    buttons.put(event.getSlot(), button);
                    event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), guiInventory, event.getSlot(), event));
                }
            } else {
                event.setCancelled(false);
                if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                    int slot = event.getCurrentItem() != null ? InventoryUtils.firstSimilar(event.getView().getTopInventory(), event.getCurrentItem()) : -1;
                    if (slot == -1) {
                        slot = event.getView().getTopInventory().firstEmpty();
                    }
                    Button<C> button = guiHandler.getButton(guiWindow, slot);
                    if (button == null) {
                        event.setCancelled(true);
                        return;
                    }
                    event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), guiInventory, slot, event));
                }
            }
            if (guiHandler.getWindow() != null) {
                Bukkit.getScheduler().runTask(wolfyUtilities.getPlugin(), () -> guiWindow.update(guiInventory, buttons, event));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory instanceof GUIInventory && ((GUIInventory<?>) inventory).getGuiHandler().getInvAPI().equals(this)) {
            GUIInventory<C> guiInventory = (GUIInventory<C>) inventory;
            GuiHandler<C> guiHandler = guiInventory.getGuiHandler();
            if (event.getRawSlots().parallelStream().anyMatch(rawSlot -> !Objects.equals(event.getView().getInventory(rawSlot), inventory))) {
                event.setCancelled(true);
                return;
            }
            GuiWindow<C> guiWindow = guiHandler.getWindow();
            if (guiWindow != null) {
                GuiItemDragEvent guiItemDragEvent = new GuiItemDragEvent(guiHandler, event);
                Bukkit.getPluginManager().callEvent(guiItemDragEvent);
                if (guiItemDragEvent.isCancelled()) {
                    event.setCancelled(true);
                }
                HashMap<Integer, Button<C>> buttons = new HashMap<>();
                for (int slot : event.getInventorySlots()) {
                    Button<C> button = guiHandler.getButton(guiWindow, slot);
                    if (button == null) {
                        event.setCancelled(true);
                        return;
                    }
                    buttons.put(slot, button);
                }
                for (Map.Entry<Integer, Button<C>> button : buttons.entrySet()) {
                    event.setCancelled(executeButton(button.getValue(), guiHandler, (Player) event.getWhoClicked(), guiInventory, button.getKey(), event));
                }
                if (guiHandler.getWindow() != null) {
                    Bukkit.getScheduler().runTask(wolfyUtilities.getPlugin(), () -> guiWindow.update(guiInventory, buttons, event));
                }
            }
        }
    }

    private boolean executeButton(Button<C> button, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) {
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
            GuiHandler<C> guiHandler = getGuiHandler(event.getPlayer());
            if (guiHandler.isChatEventActive()) {
                final String message = event.getMessage();
                //Wraps normal written message into command to be executed
                Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wui" + ' ' + getPlugin().getName() + ' ' + event.getPlayer().getUniqueId().toString() + ' ' + message));
                event.setCancelled(true);
            }
        }
    }

}
