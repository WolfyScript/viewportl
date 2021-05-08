package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public abstract class RecipeUtil extends UtilComponent {

    protected RecipeUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Get the Iterator of the specific recipe type.
     * Other than the Bukkit Recipe Iterator this Iterator only contains the recipes of the specified type.
     *
     * @param recipeType The recipe type to get the iterator for.
     * @return The iterator of the recipe type.
     */
    public abstract @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType);

}
