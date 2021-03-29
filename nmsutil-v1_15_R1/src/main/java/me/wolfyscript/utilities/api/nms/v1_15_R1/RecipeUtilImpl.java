package me.wolfyscript.utilities.api.nms.v1_15_R1;

import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.api.nms.v1_15_R1.inventory.RecipeIterator;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class RecipeUtilImpl extends me.wolfyscript.utilities.api.nms.RecipeUtil {

    protected RecipeUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType) {
        return new RecipeIterator(recipeType);
    }
}
