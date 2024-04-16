package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1;

import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.RecipeUtil;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.RecipeType;
import com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1.inventory.RecipeIterator;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class RecipeUtilImpl extends RecipeUtil {

    protected RecipeUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType) {
        return new RecipeIterator(recipeType);
    }

}
