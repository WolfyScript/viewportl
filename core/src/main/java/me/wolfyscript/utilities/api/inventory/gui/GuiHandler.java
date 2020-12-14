package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuiHandler<C extends CustomCache> implements Listener {

    private final WolfyUtilities api;
    private final InventoryAPI<C> invAPI;
    private final UUID uuid;
    private final HashMap<String, List<String>> clusterHistory = new HashMap<>();
    private ChatInputAction chatInputAction = null;
    private String currentGuiCluster = "";
    private boolean isWindowOpen = false;
    private boolean helpEnabled = false;
    private boolean changingInv = false;

    private final C customCache;

    public GuiHandler(Player player, WolfyUtilities api, InventoryAPI<C> invAPI, C customCache) {
        this.api = api;
        this.invAPI = invAPI;
        this.uuid = player.getUniqueId();
        this.customCache = customCache;
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
    }

    public void setChangingInv(boolean changingInv) {
        this.changingInv = changingInv;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public InventoryAPI<C> getInvAPI() {
        return invAPI;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    public String getCurrentGuiCluster() {
        return currentGuiCluster;
    }

    public void setCurrentGuiCluster(String currentGuiCluster) {
        this.currentGuiCluster = currentGuiCluster;
    }

    /*
    Return true if a GuiWindow is open.
    */
    public boolean isWindowOpen() {
        return isWindowOpen;
    }

    public boolean verifyInventory(Inventory inventory) {
        return isWindowOpen() && getCurrentInv() != null && getCurrentInv().getInventory(this).equals(inventory);
    }

    /*
    Reloads the GuiWindow of the GuiCluster.
     */
    public void reloadInv(NamespacedKey namespacedKey) {
        List<String> history = clusterHistory.getOrDefault(namespacedKey.getNamespace(), new ArrayList<>());
        history.remove(history.get(history.size() - 1));
        clusterHistory.put(namespacedKey.getNamespace(), history);
        changeToInv(namespacedKey);
    }

    /*
    Gets the current GuiWindow. If the Gui isn't opened then the latest GuiWindow is returned.
     */
    @Nullable
    public GuiWindow<C> getCurrentInv(String clusterID) {
        if (clusterHistory.get(clusterID) != null && clusterHistory.get(clusterID).size() > 0) {
            return invAPI.getGuiWindow(new NamespacedKey(clusterID, clusterHistory.get(clusterID).get(clusterHistory.get(clusterID).size() - 1)));
        }
        return null;
    }

    @Nullable
    public GuiWindow<C> getCurrentInv() {
        return getCurrentInv(getCurrentGuiCluster());
    }

    /*
    Gets the previous GuiWindow that was open. If there is non null is returned!
     */
    @Nullable
    public GuiWindow<C> getPreviousInv(String clusterID) {
        return getPreviousInv(clusterID, 2);
    }

    /*
    Gets the previous GuiWindow that was open. If there is non null is returned!
     */
    @Nullable
    public GuiWindow<C> getPreviousInv(String clusterID, int stepsBack) {
        List<String> history = clusterHistory.getOrDefault(clusterID, new ArrayList<>());
        if (history.size() > stepsBack) {
            return invAPI.getGuiWindow(new NamespacedKey(clusterID, history.get(history.size() - (stepsBack + 1))));
        }
        return null;
    }

    public GuiWindow<C> getPreviousInv() {
        return getPreviousInv(getCurrentGuiCluster());
    }

    public GuiWindow<C> getPreviousInv(int stepsBack) {
        return getPreviousInv(getCurrentGuiCluster(), stepsBack);
    }

    public void openPreviousInv() {
        openPreviousInv(getCurrentGuiCluster());
    }

    public void openPreviousInv(String clusterID) {
        openPreviousInv(clusterID, 1);
    }

    public void openPreviousInv(int stepsBack) {
        openPreviousInv(getCurrentGuiCluster(), stepsBack);
    }

    public void openPreviousInv(String clusterID, int stepsBack) {
        List<String> history = clusterHistory.getOrDefault(clusterID, new ArrayList<>());
        for (int i = 0; i < stepsBack; i++) {
            if (!history.isEmpty()) {
                history.remove(history.size() - 1);
            }
        }
        clusterHistory.put(clusterID, history);
        if (history.isEmpty()) {
            openCluster(clusterID);
        } else {
            changeToInv(new NamespacedKey(clusterID, history.get(history.size() - 1)));
        }
    }

    public HashMap<String, List<String>> getClusterHistory() {
        return clusterHistory;
    }

    /*
        Opens the specific GuiWindow in the current GuiCluster.
         */
    public void changeToInv(String guiWindowID) {
        changeToInv(new NamespacedKey(getCurrentGuiCluster(), guiWindowID));
    }

    /*
    Opens the specific GuiWindow in the specific GuiCluster.
     */
    public void changeToInv(NamespacedKey namespacedKey) {
        Bukkit.getScheduler().runTask(getApi().getPlugin(), () -> {
            Player player1 = getPlayer();
            if (api.getPermissions().hasPermission(player1, (api.getPlugin().getName() + ".inv." + namespacedKey.getNamespace() + "." + namespacedKey.getKey()))) {
                List<String> history = clusterHistory.getOrDefault(namespacedKey.getNamespace(), new ArrayList<>());
                if (getCurrentInv(namespacedKey.getNamespace()) == null || !getCurrentInv(namespacedKey.getNamespace()).getNamespacedKey().equals(namespacedKey)) {
                    history.add(namespacedKey.getKey());
                }
                clusterHistory.put(namespacedKey.getNamespace(), history);
                if (invAPI.getGuiWindow(namespacedKey) != null) {
                    currentGuiCluster = namespacedKey.getNamespace();
                    isWindowOpen = true;
                    invAPI.getGuiWindow(namespacedKey).update(this, null, null, true);
                }
                return;
            }
            api.getChat().sendPlayerMessage(player1, "§4You don't have the permission §c" + (api.getPlugin().getName() + ".inv." + namespacedKey.getNamespace() + "." + namespacedKey.getKey()));
        });
    }

    /*
    Opens the current GuiCluster with the latest GuiWindow that was open.
     */
    public void openCluster() {
        openCluster(getCurrentGuiCluster());
    }

    /**
     * Opens the GuiCluster with the latest GuiWindow that was open.
     */
    public void openCluster(String clusterID) {
        NamespacedKey guiWindowID = invAPI.getGuiCluster(clusterID).getEntry();
        if (getCurrentInv(clusterID) != null) {
            guiWindowID = getCurrentInv(clusterID).getNamespacedKey();
        }
        changeToInv(guiWindowID);
    }

    public boolean isChatEventActive() {
        return getChatInputAction() != null;
    }

    public ChatInputAction getChatInputAction() {
        return chatInputAction;
    }

    public void setChatInputAction(ChatInputAction chatInputAction) {
        this.chatInputAction = chatInputAction;
    }

    public void close() {
        this.isWindowOpen = false;
        Player player = getPlayer();
        if (player != null) player.closeInventory();
    }

    public void setHelpEnabled(boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
    }

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    public void setButton(GuiWindow<C> guiWindow, int slot, String id) {
        TreeMap<Integer, String> buttons = customCache.getButtons(guiWindow);
        buttons.put(slot, id);
        customCache.setButtons(guiWindow, buttons);
    }

    public Button<C> getButton(GuiWindow<C> guiWindow, int slot) {
        String id = customCache.getButtons(guiWindow).get(slot);
        if (id != null && !id.isEmpty() && id.contains(":")) {
            return invAPI.getButton(id.split(":")[0], id.split(":")[1]);
        }
        return guiWindow.getButton(id);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer().getUniqueId().equals(uuid)) {
            if (!clusterHistory.isEmpty() && isWindowOpen() && !changingInv) {
                if (getCurrentInv().onClose(this, event.getView())) {
                    Bukkit.getScheduler().runTask(getApi().getPlugin(), (Runnable) this::openCluster);
                } else {
                    this.isWindowOpen = false;
                }
            }
        }
    }

    public void cancelChatEvent() {
        setChatInputAction(null);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/wua")) {
            Player player = event.getPlayer();
            if (player.getUniqueId().equals(uuid)) {
                if (isChatEventActive()) {
                    cancelChatEvent();
                }
            }
        }
    }

    public C getCustomCache() {
        return customCache;
    }

}
