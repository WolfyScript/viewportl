package me.wolfyscript.utilities.util.inventory.item_builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder extends AbstractItemBuilder<ItemBuilder> {

    private final ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack){
        super(ItemBuilder.class);
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material){
        super(ItemBuilder.class);
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
