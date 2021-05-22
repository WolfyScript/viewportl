package me.wolfyscript.utilities.api.nms;

import java.io.IOException;

public abstract class ItemUtil extends UtilComponent {

    protected ItemUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     * Or to save it in the vanilla style Json String.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item in NMS style.
     */
    public abstract String getItemStackJson(org.bukkit.inventory.ItemStack itemStack);

    /**
     * Converts the NMS Json Sting to an {@link org.bukkit.inventory.ItemStack}.
     *
     * @param json the NMS json to convert
     * @return the ItemStack representation of the Json String
     */
    public abstract org.bukkit.inventory.ItemStack getJsonItemStack(String json);

    public abstract String getItemStackBase64(org.bukkit.inventory.ItemStack itemStack) throws IOException;

    public abstract org.bukkit.inventory.ItemStack getBase64ItemStack(String data) throws IOException;

    public abstract org.bukkit.inventory.ItemStack getBase64ItemStack(byte[] bytes) throws IOException;
}
