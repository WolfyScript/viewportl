package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiHandler implements Listener {

    private WolfyUtilities api;
    private Player player;
    private boolean changingInv = false;
    private int testChatID = -1;
    private String uuid;

    private List<String> pageHistory = new ArrayList<>();

    private boolean helpEnabled = false;

    public GuiHandler(Player player, WolfyUtilities api) {
        this.api = api;
        this.player = player;
        this.uuid = player.getUniqueId().toString();
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
    }

    public boolean isChangingInv() {
        return changingInv;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public Player getPlayer() {
        return player;
    }

    public void testForNewPlayerInstance() {
        this.player = Bukkit.getPlayer(UUID.fromString(uuid));
    }

    public boolean verifyInv() {
        if (!pageHistory.isEmpty()) {
            return !pageHistory.get(pageHistory.size() - 1).equals("none");
        }
        return false;
    }

    public void reloadInv(String inv) {
        if (!pageHistory.isEmpty()) {
            pageHistory.remove(pageHistory.size() - 1);
        }
        changeToInv(inv);
    }

    public GuiWindow getCurrentInv() {
        if (!pageHistory.isEmpty()) {
            if(pageHistory.size() > 1){
                return getApi().getInventoryAPI().getGuiWindow(pageHistory.get(pageHistory.size() - 1));
            }
            return getApi().getInventoryAPI().getGuiWindow(pageHistory.get(0));
        }
        return null;
    }

    public GuiWindow getLastInv() {
        if (!pageHistory.isEmpty() && pageHistory.size() > 1) {
            return getApi().getInventoryAPI().getGuiWindow(pageHistory.get(pageHistory.size() - 2));
        }
        return null;
    }

    public void changeToInv(String inv) {
        changingInv = true;
        player.closeInventory();
        if (WolfyUtilities.hasPermission(player, getApi().getPlugin().getDescription().getName().toLowerCase() + ".inv." + inv.toLowerCase())) {
            if (!pageHistory.isEmpty()) {
                if (!pageHistory.get(pageHistory.size() - 1).equals(inv)) {
                    if (pageHistory.get(pageHistory.size() - 1).equals("none")) {
                        pageHistory.remove(pageHistory.size() - 1);
                    }
                    pageHistory.add(inv);
                }
            } else {
                pageHistory.add(inv);
            }
            if (api.getInventoryAPI().getGuiWindow(inv) != null) {
                GuiUpdateEvent event = new GuiUpdateEvent(this, api.getInventoryAPI().getGuiWindow(inv));
                Bukkit.getPluginManager().callEvent(event);
                api.getInventoryAPI().getGuiWindow(inv).setCachedInventorie(this, event.getInventory());
                player.openInventory(event.getInventory());
            }
        } else {
            api.sendPlayerMessage(player, "§4You don't have the permission §c" + getApi().getPlugin().getDescription().getName().toLowerCase() + ".inv." + inv.toLowerCase());
        }
        changingInv = false;
    }

    public void openLastInv() {
        String inv = pageHistory.get((pageHistory.size() - 2) > -1 ? pageHistory.size() - 2 : 0);
        pageHistory.remove(pageHistory.size() - 1);
        changeToInv(inv);
    }

    public String verifyItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                String[] splitted = item.getItemMeta().getDisplayName().split("§:§:");
                if (splitted.length >= 3) {
                    if (WolfyUtilities.unhideString(splitted[splitted.length - 1]).equals(Main.getMainUtil().getConfigAPI().getConfig("main_config").getString("securityCode"))) {
                        return WolfyUtilities.unhideString(splitted[splitted.length - 2]);
                    }
                }
            }
        }
        return "";
    }

    public ItemStack getItem(String namespace, String id) {
        return getApi().getInventoryAPI().getItem(namespace, id, helpEnabled);
    }

    public int getTestChatID() {
        return testChatID;
    }

    public void setTestChatID(int testChatID) {
        this.testChatID = testChatID;
    }

    public boolean isChatEventActive(){
        return getTestChatID() > -1;
    }

    public void cancelChatEvent(){
        setTestChatID(-1);
    }

    public void close(){
        changeToInv("none");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != null && !pageHistory.isEmpty() && verifyInv()) {
            if (player.getOpenInventory() == null || !changingInv) {
                pageHistory.add("none");
            }
        }
    }
}
