package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickData;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookUtil {

    private final WolfyUtilities wolfyUtilities;
    private final Chat chat;

    public BookUtil(WolfyUtilities wolfyUtilities){
        this.wolfyUtilities = wolfyUtilities;
        this.chat = wolfyUtilities.getChat();
    }

    public void openBook(Player player, String author, String title, boolean editable, ClickData[]... clickData) {
        ItemStack itemStack = new ItemStack(editable ? Material.BOOK : Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setAuthor(author);
        bookMeta.setTitle(title);
        for (ClickData[] d : clickData) {
            TextComponent[] textComponents = chat.getActionMessage("", player, d);
            bookMeta.spigot().addPage(textComponents);
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
    }

    public void openBook(Player player, boolean editable, ClickData[]... clickData) {
        openBook(player, "WolfyUtilities", "Blank", editable, clickData);
    }
}
