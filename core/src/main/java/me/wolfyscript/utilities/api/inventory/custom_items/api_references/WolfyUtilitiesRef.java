package me.wolfyscript.utilities.api.inventory.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItems;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WolfyUtilitiesRef extends APIReference {

    private final NamespacedKey namespacedKey;

    public WolfyUtilitiesRef(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public ItemStack getLinkedItem() {
        CustomItem customItem = CustomItems.getCustomItem(namespacedKey);
        if (customItem != null) {
            return customItem.create();
        }
        System.out.println("Couldn't find CustomItem for " + namespacedKey.toString());
        return null;
    }

    @Override
    public ItemStack getIdItem() {
        ItemStack itemStack = CustomItems.getCustomItem(namespacedKey).create();
        if (itemStack.getType().equals(Material.AIR)) {
            return itemStack;
        }
        ItemStack idItem = new ItemStack(itemStack);
        if (this.namespacedKey != null) {
            ItemMeta idItemMeta = idItem.getItemMeta();
            List<String> lore = idItemMeta.hasLore() ? idItemMeta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add("§7[§3wolfyutilities§r§7]");
            lore.add("§3" + this.namespacedKey.toString());
            idItemMeta.setLore(lore);
            idItem.setItemMeta(idItemMeta);
        }
        return idItem;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (namespacedKey != null) {
            gen.writeStringField("wolfyutilities", namespacedKey.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WolfyUtilitiesRef)) return false;
        if (!super.equals(o)) return false;
        WolfyUtilitiesRef that = (WolfyUtilitiesRef) o;
        return Objects.equals(namespacedKey, that.namespacedKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), namespacedKey);
    }
}
