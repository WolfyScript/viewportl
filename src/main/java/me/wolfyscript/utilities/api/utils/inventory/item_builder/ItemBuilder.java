package me.wolfyscript.utilities.api.utils.inventory.item_builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder extends AbstractItemBuilder<ItemBuilder> {

    private final ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack){
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material){
        this.itemStack = new ItemStack(material);
    }

    @Override
    protected ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public ItemStack create() {
        return itemStack;
    }
}
