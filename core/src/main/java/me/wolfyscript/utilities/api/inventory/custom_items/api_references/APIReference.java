package me.wolfyscript.utilities.api.inventory.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public abstract class APIReference {

    protected int amount = 0;

    /**
     * @return the ItemStack of the API
     */
    public abstract ItemStack getLinkedItem();

    /**
     * Use this method inside of GUIs that you need to handle APIs that don't save NamespacedKeys inside the ItemStack PersistentData!
     *
     * @return a ItemStack of the API with additional PersistentDataHolder that contains the NamespacedKey or other value of the API
     */
    public abstract ItemStack getIdItem();

    public int getAmount() {
        return amount > 0 ? amount : getLinkedItem().getAmount();
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    /**
     * Used to serialize the APIReferenc Object to Json
     *
     * @param gen the initial JsonGenerator containing the custom amount field
     * @return the initial object with the properties of this APIReference
     */
    public abstract void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APIReference that = (APIReference) o;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
